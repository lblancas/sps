// main.rs
use actix_web::{App, HttpServer,web};
use dotenvy::dotenv;
mod model;
mod config;
mod controller;
mod error;
mod service_get_contact_by_hierachy;
mod service_get_count_by_hierachy;
mod service_get_contact_by_id;
mod jwtmiddleware;
use crate::jwtmiddleware::JwtUserIdMiddleware;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    dotenv().ok();
    env_logger::init();

    let app_config = config::load_env_from_profile().await;
    HttpServer::new(move || {
        App::new()
            .wrap(JwtUserIdMiddleware)
            .app_data(web::Data::new(app_config.clone()))
            .configure(controller::configure_routes)
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}