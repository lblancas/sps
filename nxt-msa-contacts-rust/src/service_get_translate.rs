use crate::model::{ContactDTO, ContactRequestDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result;


pub async fn get(
    pool: &PgPool,
    query: &QueryParams,
) -> Result<Option<ContactDTO>> {
    let result = sqlx::query_as!(
        ContactDTO,
        r#"
        SELECT CASE
               WHEN COUNT(contact_id) > 0 THEN MAX(contact_id)
               ELSE NULL
           END
           FROM hierarchy.bt_contacts
           WHERE email = $1
        "#,
        query.email.unwrap_or(1) as i32
    )
    .fetch_optional(pool)
    .await?;
    Ok(result)
}

