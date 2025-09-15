use sqlx::PgPool;
use anyhow::Result;

pub async fn get_count(pool: &PgPool, hierarchy_id: i32) -> Result<usize> {
    let row: (i64,) = sqlx::query_as(
        r#"
        SELECT count(1)
        FROM hierarchy.bt_contacts 
        WHERE hierarchy_id = $1 AND status = 1
        "#
    )
    .bind(hierarchy_id)
    .fetch_one(pool)
    .await?;

    Ok(row.0 as usize)
}
