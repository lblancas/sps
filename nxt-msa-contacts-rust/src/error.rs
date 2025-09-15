use actix_web::{HttpResponse, ResponseError};
use std::fmt;

#[derive(Debug)]
pub enum HierarchyContactException {
    BadRequest(String),
    InternalServerError(String),
}

impl fmt::Display for HierarchyContactException {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            HierarchyContactException::BadRequest(msg) => write!(f, "Bad Request: {}", msg),
            HierarchyContactException::InternalServerError(msg) => write!(f, "Internal Error: {}", msg),
        }
    }
}

impl ResponseError for HierarchyContactException {
    fn error_response(&self) -> HttpResponse {
        match self {
            HierarchyContactException::BadRequest(msg) => HttpResponse::BadRequest().body(msg.clone()),
            HierarchyContactException::InternalServerError(msg) => HttpResponse::InternalServerError().body(msg.clone()),
        }
    }
}

impl From<anyhow::Error> for HierarchyContactException {
    fn from(err: anyhow::Error) -> Self {
        HierarchyContactException::InternalServerError(err.to_string())
    }
}
