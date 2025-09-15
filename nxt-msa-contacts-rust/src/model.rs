use serde::{Deserialize, Serialize};
use sqlx::FromRow;

#[derive(Debug, Deserialize, Serialize)]
pub struct QueryParams {
    pub page: Option<i32>,
    pub size: Option<i32>,
    pub hierarchy: Option<i32>,
    pub contact: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize, FromRow)]
pub struct ContactDTO {
    pub contact_id: i32,
    pub first_name: String,
    pub last_name: String,
    pub contact_type: Option<String>,
    pub email: Option<String>,
    pub phone_number: Option<String>,
    pub mobile_number: Option<String>,
    pub status: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ContactRequestDTO {
    pub hierarchy_id: u32,
    pub first_name: String,
    pub last_name: String,
    pub contact_type: String,
    pub email: String,
    pub phone_number: String,
    pub mobile_number: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    #[serde(rename = "custom:iduser")]
    pub iduser: Option<String>,
    #[serde(rename = "custom:role")]
    pub role: Option<String>,
    #[serde(rename = "given_name")]
    pub given_name: Option<String>,
    #[serde(rename = "family_name")]
    pub family_name: Option<String>,
    pub exp: usize,
}

// DTO principal de respuesta genérica
#[derive(Debug, Serialize, Deserialize)]
pub struct ResponseDTO<T> {
    pub r#type: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub clazz: Option<T>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub list: Option<T>,
    pub response_code: ResponseCodeDTO,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub page: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub size: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub total_pages: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub total_elements: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ResponseCodeDTO {
    pub code: String,
    pub message: String,
    pub html_message: String,
    pub solution: String,
    pub category: String,
}

// === Builders ===

pub struct ResponseDTOBuilder<T> {
    r#type: Option<String>,
    clazz: Option<T>,
    list: Option<T>,
    response_code: Option<ResponseCodeDTO>,
    page: Option<i32>,
    size: Option<i32>,
    total_pages: Option<i32>,
    total_elements: Option<i32>,
}

pub struct ResponseCodeDTOBuilder {
    code: Option<String>,
    message: Option<String>,
    html_message: Option<String>,
    solution: Option<String>,
    category: Option<String>,
}

// Implementación manual para evitar requerir T: Default
impl<T> ResponseDTOBuilder<T> {
    pub fn new() -> Self {
        Self {
            r#type: None,
            clazz: None,
            list: None,
            response_code: None,
            page: None,
            size: None,
            total_pages: None,
            total_elements: None,
        }
    }

    pub fn r#type(mut self, type_value: impl Into<String>) -> Self {
        self.r#type = Some(type_value.into());
        self
    }

    pub fn clazz(mut self, clazz: T) -> Self {
        self.clazz = Some(clazz);
        self
    }

    pub fn list(mut self, list: T) -> Self {
        self.list = Some(list);
        self
    }

    pub fn response_code(mut self, response_code: ResponseCodeDTO) -> Self {
        self.response_code = Some(response_code);
        self
    }

    pub fn page(mut self, page: i32) -> Self {
        self.page = Some(page);
        self
    }

    pub fn size(mut self, size: i32) -> Self {
        self.size = Some(size);
        self
    }

    pub fn total_pages(mut self, total_pages: i32) -> Self {
        self.total_pages = Some(total_pages);
        self
    }

    pub fn total_elements(mut self, total_elements: i32) -> Self {
        self.total_elements = Some(total_elements);
        self
    }

    pub fn build(self) -> Result<ResponseDTO<T>, &'static str> {
        Ok(ResponseDTO {
            r#type: self.r#type.ok_or("type is required")?,
            clazz: self.clazz,
            list: self.list,
            response_code: self.response_code.ok_or("response_code is required")?,
            page: self.page,
            size: self.size,
            total_pages: self.total_pages,
            total_elements: self.total_elements,
        })
    }
}

impl ResponseCodeDTOBuilder {
    pub fn new() -> Self {
        Self {
            code: None,
            message: None,
            html_message: None,
            solution: None,
            category: None,
        }
    }

    pub fn code(mut self, code: impl Into<String>) -> Self {
        self.code = Some(code.into());
        self
    }

    pub fn message(mut self, message: impl Into<String>) -> Self {
        self.message = Some(message.into());
        self
    }

    pub fn html_message(mut self, html_message: impl Into<String>) -> Self {
        self.html_message = Some(html_message.into());
        self
    }

    pub fn solution(mut self, solution: impl Into<String>) -> Self {
        self.solution = Some(solution.into());
        self
    }

    pub fn category(mut self, category: impl Into<String>) -> Self {
        self.category = Some(category.into());
        self
    }

    pub fn build(self) -> Result<ResponseCodeDTO, &'static str> {
        Ok(ResponseCodeDTO {
            code: self.code.ok_or("code is required")?,
            message: self.message.ok_or("message is required")?,
            html_message: self.html_message.ok_or("html_message is required")?,
            solution: self.solution.ok_or("solution is required")?,
            category: self.category.ok_or("category is required")?,
        })
    }
}

// Accesos estáticos convenientes
impl<T> ResponseDTO<T> {
    pub fn builder() -> ResponseDTOBuilder<T> {
        ResponseDTOBuilder::new()
    }
}

impl ResponseCodeDTO {
    pub fn builder() -> ResponseCodeDTOBuilder {
        ResponseCodeDTOBuilder::new()
    }
}
