use actix_service::Service;
use actix_web::{
    dev::{ServiceRequest, ServiceResponse, Transform},
    Error, HttpMessage,
};
use futures_util::future::{ready, LocalBoxFuture, Ready as FutureReady};
use futures_util::FutureExt;
use std::rc::Rc;

pub struct ExtractUserId;

impl<S, B> Transform<S, ServiceRequest> for ExtractUserId
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type InitError = ();
    type Transform = ExtractUserIdMiddleware<S>;
    type Future = FutureReady<Result<Self::Transform, Self::InitError>>;
    fn new_transform(&self, service: S) -> Self::Future {
        ready(Ok(ExtractUserIdMiddleware {
            service: Rc::new(service),
        }))
    }
}

pub struct ExtractUserIdMiddleware<S> {
    service: Rc<S>,
}

impl<S, B> Service<ServiceRequest> for ExtractUserIdMiddleware<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error> + 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    fn poll_ready(
        &self,
        ctx: &mut std::task::Context<'_>,
    ) -> std::task::Poll<Result<(), Self::Error>> {
        self.service.poll_ready(ctx)
    }

    fn call(&self, req: ServiceRequest) -> Self::Future {
        if let Some(user_id) = req.headers().get("X-User-Id") {
            if let Ok(id_str) = user_id.to_str() {
                req.extensions_mut().insert(id_str.to_string());
            }
        }

        let fut = self.service.call(req);
        async move {
            let res = fut.await?;
            Ok(res)
        }
        .boxed_local()
    }
}