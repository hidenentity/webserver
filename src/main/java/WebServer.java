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

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;


public class WebServer extends AbstractVerticle {
    @Override
    public void start(Future<Void> fut) throws IllegalAccessException, SQLException, InstantiationException {

        PersistenceInit();

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
                .end(Json.encodePrettily(dataService.getDocs()));

    }


    private void addOne(RoutingContext routingContext) {
        Doc doc = Json.decodeValue(routingContext.getBodyAsString(),
                Doc.class);
        doc = dataService.addDoc(doc);
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
            Long idAsLong = Long.valueOf(id);
            dataService.deleteDoc(idAsLong);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Long idAsLong = Long.valueOf(id);
            Doc doc = dataService.getDoc(idAsLong);
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
        Doc doc = Json.decodeValue(routingContext.getBodyAsString(), Doc.class);

        if (id == null || doc == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Long idAsLong = Long.valueOf(id);
            doc = dataService.updateDoc(doc, idAsLong);
            if (doc == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(doc));
            }
        }
    }

    // Store our product
    private Map<Long, Doc> docs = new LinkedHashMap<>();

    private DataService dataService;

    private void PersistenceInit() throws IllegalAccessException, SQLException, InstantiationException {
        JPAInit.prepareDatabase();
        dataService = new DataService();
    }
}

