use actix_web::{get, post, delete, put, web, HttpRequest, Responder, HttpResponse};
use crate::model::{QueryParams, ContactDTO, ContactRequestDTO, ResponseDTO};
use crate::error::HierarchyContactException;
use actix_web::HttpMessage;
use crate::jwtmiddleware::UserContext;
use crate::model::{ResponseCodeDTO, ResponseDTOBuilder};
use crate::config::Config;

use crate::service_get_contact_by_hierachy::getContacts;
use crate::service_get_count_by_hierachy::get_count;
use crate::service_get_contact_by_id::{getContact};

#[get("/api/v1/hierarchy-contact")]
pub async fn get_by_page(
    req: HttpRequest,
    query: web::Query<QueryParams>,
    config: web::Data<Config>,
) -> Result<impl Responder, HierarchyContactException> {
    let lang = get_language(&req);
    let user_id = get_user_id(&req);
    println!("Usuario que modifica {} ", user_id);

    let hierarchy_id = query.hierarchy.ok_or_else(|| HierarchyContactException::BadRequest("Missing hierarchyId".into()))?;
    let page = query.page.ok_or_else(|| HierarchyContactException::BadRequest("Missing page".into()))?;
    let size = query.size.ok_or_else(|| HierarchyContactException::BadRequest("Missing size".into()))?;

    let total_elements = get_count(&config.db_pool, hierarchy_id).await?;
    if total_elements == 0 {
        return Ok(web::Json(create_empty_response("NXT_MSA_EMPTY_RESULT", "No se encontraron contactos.", "<p>No se encontraron resultados.</p>")));
    }
    if (total_elements as i32) < size {
       let tquery = QueryParams {
            page: Some(page),
            size: Some(total_elements as i32),
            hierarchy: Some(hierarchy_id),
            contact: None,  // o Some(0) si necesitas un valor
        };
    }
    

    let total_pages = (total_elements as i32 + size - 1) / size;
    let contacts = getContacts(&config.db_pool, &query).await?;

    let response = ResponseDTOBuilder::new()
        .r#type("HierarchyContactResponse")
        .list(contacts)
        .page(page)
        .size(size)
        .total_pages(total_pages)
        .total_elements(total_elements as i32)
        .response_code(
            ResponseCodeDTO::builder()
                .code("NXT_MSA_SUCCESS")
                .message("Consulta exitosa")
                .html_message("<p>Consulta exitosa</p>")
                .solution("")
                .category("INFO")
                .build()
                .unwrap(),
        )
        .build()
        .unwrap();

    Ok(web::Json(response))
}

#[get("/api/v1/hierarchy-contact/byId")]
pub async fn get_by_hierarchy_contact(
    req: HttpRequest,
    query: web::Query<QueryParams>,
    config: web::Data<Config>,
) -> Result<impl Responder, HierarchyContactException> {
    let lang = get_language(&req);
    let user_id = get_user_id(&req);

    println!("lang: {}", lang);
    println!("user_id: {}", user_id);
    println!("Hierarchy: {:?}", query.hierarchy);
    println!("Contact: {:?}", query.contact);
    let tamano : i32 = 1;
    let contact = getContact(&config.db_pool, &query).await?;

    let response = ResponseDTOBuilder::new()
        .r#type("HierarchyContactResponse")
        .list(contact)
        .page(tamano)
        .size(tamano)
        .total_pages(tamano)
        .total_elements(tamano)
        .response_code(
            ResponseCodeDTO::builder()
                .code("NXT_MSA_SUCCESS")
                .message("Consulta exitosa")
                .html_message("<p>Consulta exitosa</p>")
                .solution("")
                .category("INFO")
                .build()
                .unwrap(),
        )
        .build()
        .unwrap();

    Ok(web::Json(response))
}

#[post("/api/v1/hierarchy-contact")]
pub async fn create_contact(
    req: HttpRequest,
    body: web::Json<ContactDTO>,
) -> Result<impl Responder, HierarchyContactException> {
    let lang = get_language(&req);
    let user_id = get_user_id(&req);
    println!("lang  {} ", lang);
    println!("user_id  {}", user_id);

    Ok(HttpResponse::Ok().json(body.into_inner()))
}

#[delete("/api/v1/hierarchy-contact")]
pub async fn delete_contact(
    req: HttpRequest,
    body: web::Json<ContactRequestDTO>,
) -> Result<impl Responder, HierarchyContactException> {
    let lang = get_language(&req);
    let user_id = get_user_id(&req);
    println!("lang  {} ", lang);
    println!("user_id  {}", user_id);

    Ok(HttpResponse::Ok().json(body.into_inner()))
}

#[put("/api/v1/hierarchy-contact")]
pub async fn update_contact(
    req: HttpRequest,
    body: web::Json<ContactDTO>,
) -> Result<impl Responder, HierarchyContactException> {
    let lang = get_language(&req);
    let user_id = get_user_id(&req);
    println!("lang  {} ", lang);
    println!("user_id  {}", user_id);

    Ok(HttpResponse::Ok().json(body.into_inner()))
}

fn get_language(req: &HttpRequest) -> String {
    req.headers()
        .get("Accept-Language")
        .and_then(|v| v.to_str().ok())
        .unwrap_or("es")
        .to_string()
}

fn get_user_id(req: &HttpRequest) -> String {
    req.extensions()
        .get::<UserContext>()
        .map(|ctx| ctx.user_id.clone())
        .unwrap_or_else(|| "anonymous".to_string())
}

fn create_empty_response(code: &str, message: &str, html: &str) -> ResponseDTO<Vec<ContactDTO>> {
    let response_code = ResponseCodeDTO::builder()
        .code(code)
        .message(message)
        .html_message(html)
        .solution("")
        .category("")
        .build()
        .expect("Error creando ResponseCodeDTO para respuesta vacía");

    ResponseDTOBuilder::new()
        .r#type("HierarchyContactResponse")
        .list(vec![])
        .page(0)
        .size(0)
        .total_pages(0)
        .total_elements(0)
        .response_code(response_code)
        .build()
        .expect("Error creando ResponseDTO vacío")
}

pub fn configure_routes(cfg: &mut web::ServiceConfig) {
    cfg
        .service(get_by_page)
        .service(get_by_hierarchy_contact)
        .service(create_contact)
        .service(update_contact)
        .service(delete_contact);
}

macro_rules! unwrap_param {
    ($param:expr, $name:expr) => {
        $param.ok_or_else(|| HierarchyContactException::BadRequest(format!("Missing {}", $name)))?
    };
}
