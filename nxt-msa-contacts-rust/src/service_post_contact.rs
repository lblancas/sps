use crate::model::{ContactDTO, ContactRequestDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result;

pub async fn post(
    pool: &PgPool,
    dto: &ContactDTO,
) -> Result<()> {
    sqlx::query!(
        r#"
        INSERT INTO hierarchy.bt_contacts
          ( hierarchy_id, first_name, last_name, contact_type, email, phone_number, mobile_number, creation_date ,status)
          VALUES(
          $1, $2, $3, $4, $5, $6, $7, now(), 1) RETURNING contact_id
        "#,
        dto.hierarchy_id,
        dto.first_name,
        dto.last_name,
        dto.contact_type,
        dto.email,
        dto.phone_number,
        dto.mobile_number
    )
    .execute(pool)
    .await?;
    Ok(())
}
