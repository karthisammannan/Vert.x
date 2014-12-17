package second;

import java.util.ArrayList;
import java.util.Collections;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.DecodeException;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class sort extends Verticle {
	public void start() {
		RouteMatcher matcher = new RouteMatcher();

		matcher.post("/sort", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.bodyHandler(new Handler<Buffer>() {
					public void handle(Buffer data) {
						try {
							if (data.length() > 20000)
								throw new Exception("Unable to handle");
							JsonArray inputJsonArray = new JsonArray(data
									.toString());
							@SuppressWarnings("unchecked")
							ArrayList<Integer> inputList = (ArrayList<Integer>) inputJsonArray
									.toList();
							Collections.sort(inputList);
							JsonArray result = new JsonArray(inputList);
							req.response()
									.putHeader("Content-Type",
											"application/json")
									.end(result.encode());
						} catch (Exception e) {
							req.response().setStatusCode(400)
									.setStatusMessage(e.getMessage()).end();
						
						}
					}
				});
			}

		});
		matcher.post("/reverse", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.bodyHandler(new Handler<Buffer>() {
					public void handle(Buffer data) {
						try {
							JsonArray inputJsonArray = new JsonArray(data.toString());
							@SuppressWarnings("unchecked")
							ArrayList<Object> inputList = (ArrayList<Object>) inputJsonArray.toList();
							Collections.reverse(inputList);
							JsonArray result = new JsonArray(inputList);
							req.response()
									.putHeader("Content-Type","application/json")
									.end(result.encode());
						} catch (IllegalArgumentException|DecodeException e) {
							req.response()
							.setStatusCode(400)
							.setStatusMessage("Bad Request").end();
						}
					}
				});
			}

		});

		matcher.post("/elementsize", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.bodyHandler(new Handler<Buffer>() {
					public void handle(Buffer data) {
						int size;
						try{
							try {
								size=new JsonObject(data.toString()).size();
							} catch (DecodeException e) {
								size=new JsonArray(data.toString()).size();
							}
							JsonArray outputjsonarray=new JsonArray();
							outputjsonarray.add(size);
							req.response()
							.putHeader("Content-Type", "application/json")
							.end(outputjsonarray.encode());
						}catch(DecodeException e){
							req.response()
							.setStatusCode(400)
							.setStatusMessage("Bad Request").end();
						}
					}
				});
			}

		});
		vertx.createHttpServer().requestHandler(matcher).listen(9000);
	}
}
