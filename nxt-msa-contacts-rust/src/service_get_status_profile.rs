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
        SELECT  count(1) as total
          FROM login.lkp_policies lp
          INNER JOIN login.role_policies rp ON lp.policy_id = rp.policy_id
          INNER JOIN login.lkp_role lr ON rp.role_id = lr.role_id
          INNER JOIN login.profile_role pr ON pr.role_id = lr.role_id
          inner join login.user_role ur  on ur.role_id = pr.role_id and pr.role_id = lr.role_id
          inner join login.lkp_users lu on ur.user_id = lu.user_id
          inner join login.lkp_modules lm on lm.module_id = lp.module_id
          WHERE  lr.role_id = :role
            and ur.user_id =:user
            and lm.name=:module
            and lp.type =:activity
            and lp.status =:statusProfile
        "#,
        query.email.unwrap_or(1) as i32
    )
    .fetch_optional(pool)
    .await?;
    Ok(result)
}

