use crate::model::{ContactDTO, ContactRequestDTO, QueryParams};
use sqlx::PgPool;
use anyhow::Result;

pub async fn delete(
    pool: &PgPool,
    dto: &ContactRequestDTO,
) -> Result<()> {
    sqlx::query!(
        r#"
        DELETE FROM contacts
        WHERE hierarchy_id = $1 AND email = $2
        "#,
        dto.hierarchy_id as i32,
        dto.email
    )
    .execute(pool)
    .await?;

    Ok(())
}
