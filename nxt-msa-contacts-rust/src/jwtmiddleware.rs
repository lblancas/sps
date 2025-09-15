use actix_service::Service;
use actix_web::{
    dev::{ServiceRequest, ServiceResponse, Transform},
    Error, HttpMessage,
};
use futures_util::future::{ok, LocalBoxFuture, Ready};
use jsonwebtoken::{decode, decode_header, Algorithm, DecodingKey, Validation};
use serde::{Deserialize, Serialize};
use std::{
    rc::Rc,
    task::{Context, Poll},
};
use reqwest::Client;

#[derive(Debug, Deserialize, Serialize)]
struct Claims {
    #[serde(rename = "custom:iduser")]
    iduser: Option<String>,
    exp: usize,
}

#[derive(Clone, Debug)]
pub struct UserContext {
    pub user_id: String,
}

pub struct JwtUserIdMiddleware;

impl<S, B> Transform<S, ServiceRequest> for JwtUserIdMiddleware
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type InitError = ();
    type Transform = JwtUserIdMiddlewareImpl<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ok(JwtUserIdMiddlewareImpl {
            service: Rc::new(service),
        })
    }
}

pub struct JwtUserIdMiddlewareImpl<S> {
    service: Rc<S>,
}

impl<S, B> Service<ServiceRequest> for JwtUserIdMiddlewareImpl<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    fn poll_ready(&self, ctx: &mut Context<'_>) -> Poll<Result<(), Self::Error>> {
        self.service.poll_ready(ctx)
    }

    fn call(&self, req: ServiceRequest) -> Self::Future {
        let fut = self.service.clone();
        let maybe_auth = req.headers().get("Authorization").cloned();
        println!("🔐 Authorization header: {:?}", maybe_auth);
        Box::pin(async move {
            if let Some(auth_header) = maybe_auth {
                if let Ok(auth_str) = auth_header.to_str() {
                    if let Some(token) = auth_str.strip_prefix("Bearer ") {
                        println!("✔️ Tiene Bearer");

                        match decode_header(token) {
                            Ok(header) => {
                                if let Some(kid) = header.kid {
                                    println!("🔑 KID extraído: {}", kid);

                                    let jwks_url = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_PnFOcVZSs/.well-known/jwks.json";

                                    match Client::new().get(jwks_url).send().await {
                                        Ok(resp) => {
                                            if let Ok(jwks) = resp.json::<serde_json::Value>().await {
                                                if let Some(keys) = jwks.get("keys").and_then(|k| k.as_array()) {
                                                    if let Some(jwk) = keys.iter().find(|k| k.get("kid").and_then(|v| v.as_str()) == Some(&kid)) {
                                                        let n = jwk.get("n").and_then(|v| v.as_str()).unwrap_or_default();
                                                        let e = jwk.get("e").and_then(|v| v.as_str()).unwrap_or_default();

                                                        if let Ok(decoding_key) = DecodingKey::from_rsa_components(n, e) {
                                                            let mut validation = Validation::new(Algorithm::RS256);
                                                            validation.validate_aud = false;
                                                            match decode::<Claims>(token, &decoding_key, &validation) {
                                                                Ok(data) => {
                                                                    if let Some(iduser) = data.claims.iduser {
                                                                        println!("🧑 ID User extraído: {:?}", iduser);
                                                                        req.extensions_mut().insert(UserContext {
                                                                            user_id: iduser,
                                                                        });
                                                                    } else {
                                                                        println!("⚠️ El claim `custom:iduser` está ausente");
                                                                    }
                                                                }
                                                                Err(err) => {
                                                                    println!("❌ Error al decodificar JWT: {:?}", err);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Err(err) => {
                                            println!("❌ Error al obtener JWKs: {:?}", err);
                                        }
                                    }
                                }
                            }
                            Err(err) => println!("❌ Error en el header JWT: {:?}", err),
                        }
                    }
                }
            }
            fut.call(req).await
        })
    }
}