import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;


public class WebServerMemory extends AbstractVerticle {
    @Override
    public void start(Future<Void> fut) {
        createSomeData();

        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        CorsHandler corsHandler = CorsHandler.create("*");
        corsHandler.allowCredentials(true);
        corsHandler.allowedMethod(HttpMethod.OPTIONS);
        corsHandler.allowedMethod(HttpMethod.GET);
        corsHandler.allowedMethod(HttpMethod.PUT);
        corsHandler.allowedMethod(HttpMethod.POST);
        corsHandler.allowedMethod(HttpMethod.DELETE);
        corsHandler.allowedHeader("Authorization");
        corsHandler.allowedHeader("www-authenticate");
        corsHandler.allowedHeader("Content-Type");

        router.route().handler(corsHandler);

        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.get("/api/docs").handler(this::getAll);
        router.route("/api/docs*").handler(BodyHandler.create());
        router.post("/api/docs").handler(this::addOne);
        router.delete("/api/docs/:id").handler(this::deleteOne);
        router.put("/api/docs/:id").handler(this::updateOne);
        router.get("/api/docs/:id").handler(this::getOne);

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }


    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(docs.values()));

    }


    private void addOne(RoutingContext routingContext) {
            final DocMemory doc = Json.decodeValue(routingContext.getBodyAsString(),
                DocMemory.class);
        docs.put(doc.getId(), doc);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(doc));

    }

    private void deleteOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            docs.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            DocMemory doc = docs.get(idAsInteger);
            if (doc == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(doc));
            }
        }
    }

    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            DocMemory doc = docs.get(idAsInteger);
            if (doc == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                doc.setNumber(json.getString("number"));
                doc.setDescription(json.getString("description"));
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(doc));
            }
        }
    }

    // Store our product
    private Map<Integer, DocMemory> docs = new LinkedHashMap<>();

    // Create some product
    private void createSomeData() {
        DocMemory firstDocMemory = new DocMemory("0000001", "My first doc");
        docs.put(firstDocMemory.getId(), firstDocMemory);
        DocMemory secondDocMemory = new DocMemory("0000002", "My second doc");
        docs.put(secondDocMemory.getId(), secondDocMemory);
    }
}

