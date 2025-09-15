use crate::model::{ContactDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result; 
pub async fn getContact(
    pool: &PgPool,
    query: &QueryParams,
) -> Result<ContactDTO> {

    let hierarchy_id = query.hierarchy.unwrap_or(1);
    let contact_id = query.contact.unwrap_or(1);

    println!("\n=== Parámetros de Consulta ===");
    println!("Hierarchy ID: {}", hierarchy_id);
    println!("Contact ID: {}", contact_id);
    println!("===========================\n");


    // Query SQL
    const SQL_QUERY: &str = r#"
        SELECT
            contact_id,
            first_name,
            last_name,
            contact_type,
            email,
            phone_number,
            mobile_number,
            creation_date,
            status
        FROM hierarchy.bt_contacts c
        WHERE c.hierarchy_id = $1 
        and c.contact_id = $2  ;
        "#;
    println!("🔍  Query :: {} ",SQL_QUERY);
    println!("🔍 Ejecutando Query...");
    let mut contact = sqlx::query_as::<_, ContactDTO>(SQL_QUERY)
        .bind(hierarchy_id as i32)
        .bind(contact_id as i32)
        .fetch_one(pool)
        .await?; 

    // Transformar nombres a mayúsculas
    contact.first_name = contact.first_name.to_uppercase();
    contact.last_name = contact.last_name.to_uppercase();

    println!("👤 Contacto Encontrado:");
    println!("  ID: {}", contact.contact_id);
    println!("  Nombre: {} {}", contact.first_name, contact.last_name);

    Ok(contact)
}
