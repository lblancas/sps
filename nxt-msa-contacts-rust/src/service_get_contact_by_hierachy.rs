use crate::model::{ContactDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result; 
pub async fn getContacts(
    pool: &PgPool,
    query: &QueryParams,
) -> Result<Vec<ContactDTO>> {

    let hierarchy_id = query.hierarchy.unwrap_or(1);
    let size = query.size.unwrap_or(10);
    let page = query.page.unwrap_or(1);
    let offset = (page - 1) * size;

    println!("\n=== Parámetros de Consulta ===");
    println!("Hierarchy ID: {}", hierarchy_id);
    println!("Página: {}", page);
    println!("Tamaño: {}", size);
    println!("Offset: {}", offset);
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
        ORDER BY contact_id  LIMIT $2 OFFSET $3 ;
        "#;
        println!("Parameters: hierarchy_id={}, size={}, offset={}", 
       hierarchy_id, size, offset);

    println!("🔍  Query :: {} ",SQL_QUERY);
    let rows = sqlx::query_as::<_, ContactDTO>(SQL_QUERY)
        .bind(hierarchy_id)
        .bind(size as i64)
        .bind(offset as i64)
        .fetch_all(pool)
        .await?;

    println!("✨ Encontrados {} contactos", rows.len());

    let transformed: Vec<ContactDTO> = rows
        .into_iter()
        .map(|mut contact| {
            contact.first_name = contact.first_name.to_uppercase();
            contact.last_name = contact.last_name.to_uppercase();

            println!("👤 {}: {} {}", 
                    contact.contact_id, 
                    contact.first_name, 
                    contact.last_name);

            contact
        })
        .collect();

    Ok(transformed)
}
