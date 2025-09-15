use crate::model::{ContactDTO, ContactRequestDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result;

pub async fn put(
    pool: &PgPool,
    dto: &ContactDTO,
) -> Result<()> {
    sqlx::query!(
        r#"
          update hierarchy.bt_contacts
          set
              first_name = $1,
              last_name = $2,
              contact_type = $3,
              email = $4,
              phone_number = $5,
              mobile_number = $6,
              status = 1,
              update_date = now()
          where contact_id = $7
          and hierarchy_id = $8
        "#,
        dto.first_name,
        dto.last_name,
        dto.contact_type,
        dto.email,
        dto.phone_number,
        dto.mobile_number,
        dto.contact_id,
        dto.hierarchy_id
    )
    .execute(pool)
    .await?;

    Ok(())
}
