// config.rs
use dotenvy::from_filename_override;
use serde::Deserialize;
use std::env;
use sqlx::PgPool;
use aws_sdk_secretsmanager::Client as SecretsManagerClient;
use sqlx::postgres::PgPoolOptions;
#[derive(Debug, Clone)]
pub struct Config {
    pub user: String,
    pub password: String,
    pub host: String,
    pub port: String,
    pub db_name: String,
    pub db_pool: PgPool
}

impl Config {
    pub fn database_url(&self) -> String {
        format!(
            "postgres://{}:{}@{}:{}/{}",
            self.user, self.password, self.host, self.port, self.db_name
        )
    }
}

#[derive(Deserialize, Debug)]
struct AwsSecret {
    username: String,
    password: String,
    host: String,
    port: String,
    db_name: String,
}

pub async fn load_env_from_profile() -> Config {
    dotenvy::dotenv().ok();

    let profile = env::var("PROFILE").unwrap_or_else(|_| "dev".to_string());
    let filename = format!(".env.{}", profile);
    println!("🔧 Cargando configuración de perfil: {}", filename);

    env::remove_var("PROFILE");
    env::remove_var("PORT");
    env::remove_var("HOST_DB");
    env::remove_var("USER_DB");
    env::remove_var("NAME_DB");
    env::remove_var("PASSWORD_DB");
    env::remove_var("SECRET_NAME");
    env::remove_var("DATABASE_URL");

    match from_filename_override(&filename) {
        Ok(_) => println!("✅ Variables de entorno cargadas desde {}", filename),
        Err(e) => println!("⚠️ No se pudo cargar {}: {}", filename, e),
    }

    let profile = env::var("PROFILE").unwrap_or_else(|_| "dev".to_string());

    println!("Se inicia configuracion  de profile {} ",profile);

    if profile == "local" {
        let port:String = env::var("PORT_DB").unwrap_or_else(|_| "5432".to_string());
        let host:String = env::var("HOST_DB").unwrap_or_else(|_| "localhost".to_string());
        let user:String = env::var("USER_DB").unwrap_or_else(|_| "postgres".to_string());
        let db_name:String = env::var("NAME_DB").unwrap_or_else(|_| "postgres".to_string());
        let password:String = env::var("PASSWORD_DB").unwrap_or_else(|_| "password".to_string());
        let db_url = format!("postgres://{}:{}@{}:{}/{}", user, password, host, port, db_name);
        let db_pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&db_url)
        .await
        .expect("❌ No se pudo conectar a la base de datos");
        
        
        println!("✅ Conexión a la base de datos exitosa");
        Config {
            user,
            password,
            host,
            port,
            db_name,
            db_pool
        }
    } else {
        let secret_name = env::var("SECRET_NAME").expect("❌ Falta SECRET_NAME en el .env");

        let aws_config = aws_config::load_defaults(aws_config::BehaviorVersion::latest()).await;
        let client = SecretsManagerClient::new(&aws_config);

        println!("🔐 Obteniendo secreto '{}' de AWS Secrets Manager...", secret_name);

        let secret_output = client
            .get_secret_value()
            .secret_id(secret_name)
            .send()
            .await
            .expect("❌ No se pudo obtener el secreto de AWS");

        let secret_string = secret_output
            .secret_string()
            .expect("❌ El secreto no contiene texto");

        let parsed: AwsSecret = serde_json::from_str(secret_string)
            .expect("❌ No se pudo parsear el JSON del secreto");

        let db_url = format!("postgres://{}:{}@{}:{}/{}", 
                parsed.username, parsed.password, parsed.host, parsed.port, parsed.db_name);
        let db_pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&db_url)
        .await
        .expect("❌ No se pudo conectar a la base de datos");

        
        println!("✅ Conexión a la base de datos exitosa");
        Config {
            user: parsed.username,
            password: parsed.password,
            host: parsed.host,
            port: parsed.port,
            db_name: parsed.db_name,
            db_pool
        }
    }
}
