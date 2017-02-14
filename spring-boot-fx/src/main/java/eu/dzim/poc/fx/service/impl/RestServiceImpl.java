package eu.dzim.poc.fx.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.dzim.poc.fx.service.RestService;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@Component
@Scope("singleton")
public class RestServiceImpl implements RestService {
	
	private static final Logger LOG = LogManager.getLogger(RestServiceImpl.class);
	
	private OkHttpClient mClient;
	private ObjectMapper mMapper = null;
	
	@PostConstruct
	private void postConstruct() {
		
		LOG.debug("PostConstruct: set up " + getClass().getName());
		
		// OkHttp 3
		// non-persistent cookie jar that also does sth. like CookiePolicy#ACCEPT_ALL
		mClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
			
			private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
			
			@Override
			public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
				cookieStore.put(url, cookies);
			}
			
			@Override
			public List<Cookie> loadForRequest(HttpUrl url) {
				List<Cookie> cookies = cookieStore.get(url);
				return cookies != null ? cookies : new ArrayList<Cookie>();
			}
		}).build();
		
		mMapper = new ObjectMapper();
	}
	
	@Value("${remote.protocol:http}")
	private String mRemoteProtocol;
	@Value("${remote.host:localhost}")
	private String mRemoteHost;
	@Value("${remote.port:8080}")
	private String mRemotePort;
	@Value("${remote.path:/rest}")
	private String mRemoteBasePath;
	
	private HttpUrl buildHttpUrl(String relativePath, Map<String, String> queryParameter) {
		HttpUrl.Builder urlBuilder = new HttpUrl.Builder().scheme(mRemoteProtocol).host(mRemoteHost);
		if (mRemotePort != null && !mRemotePort.isEmpty()) {
			urlBuilder.port(Integer.parseInt(mRemotePort));
		}
		urlBuilder.addPathSegment(mRemoteBasePath.startsWith("/") ? mRemoteBasePath.substring(1) : mRemoteBasePath);
		urlBuilder.addPathSegment(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
		if (queryParameter != null) {
			for (String parameter : queryParameter.keySet()) {
				String value = queryParameter.get(parameter);
				if (value == null)
					continue;
				urlBuilder.addQueryParameter(parameter, value);
			}
		}
		HttpUrl url = urlBuilder.build();
		LOG.debug(url);
		return url;
	}
	
	// @Override
	// public String postForm(String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter) throws IOException {
	// Response response = internalPostForm(relativePath, queryParameter, formParameter, null);
	// try {
	// return response.isSuccessful() ? response.body().string() : null;
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> VDEResponse<T> postForm(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter)
	// throws IOException {
	// Response response = internalPostForm(relativePath, queryParameter, formParameter, null);
	// try {
	// return parseJson(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> void postForm(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter,
	// Consumer<VDEResponse<T>> onResponse, Consumer<IOException> onFailure) throws IOException {
	// internalPostForm(relativePath, queryParameter, formParameter, new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// onResponse.accept(parseJson(clazz, response.body().byteStream()));
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// @Override
	// public <T> VDEResponse<List<T>> postFormList(Class<T> clazz, String relativePath, Map<String, String> queryParameter,
	// Map<String, String> formParameter) throws IOException {
	// Response response = internalPostForm(relativePath, queryParameter, formParameter, null);
	// try {
	// return parseJsonList(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> void postFormList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter,
	// Consumer<VDEResponse<List<T>>> onResponse, Consumer<IOException> onFailure) throws IOException {
	// internalPostForm(relativePath, queryParameter, formParameter, new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// try {
	// onResponse.accept(parseJsonList(clazz, response.body().byteStream()));
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// private Response internalPostForm(String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter, Callback
	// callback)
	// throws IOException {
	// FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
	// if (formParameter != null) {
	// for (String parameter : formParameter.keySet()) {
	// String value = formParameter.get(parameter);
	// if (value == null)
	// value = "";
	// formEncodingBuilder.add(parameter, value);
	// }
	// }
	// RequestBody formBody = formEncodingBuilder.build();
	// Request request = new Request.Builder().url(buildHttpUrl(relativePath, queryParameter)).post(formBody).build();
	// if (callback == null) {
	// return mClient.newCall(request).execute();
	// } else {
	// mClient.newCall(request).enqueue(callback);
	// return null;
	// }
	// }
	//
	// @Override
	// public String get(String relativePath, Map<String, String> queryParameter) throws IOException {
	// Response response = internalGet(relativePath, queryParameter, null);
	// try {
	// return response.isSuccessful() ? response.body().string() : null;
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> VDEResponse<T> get(Class<T> clazz, String relativePath, Map<String, String> queryParameter) throws IOException {
	// Response response = internalGet(relativePath, queryParameter, null);
	// try {
	// return parseJson(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> void get(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Consumer<VDEResponse<T>> onResponse,
	// Consumer<IOException> onFailure) throws IOException {
	// internalGet(relativePath, queryParameter, new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// try {
	// onResponse.accept(parseJson(clazz, response.body().byteStream()));
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// @Override
	// public <T> VDEResponse<List<T>> getList(Class<T> clazz, String relativePath, Map<String, String> queryParameter) throws IOException {
	// Response response = internalGet(relativePath, queryParameter, null);
	// try {
	// return parseJsonList(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T> void getList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Consumer<VDEResponse<List<T>>> onResponse,
	// Consumer<IOException> onFailure) throws IOException {
	// internalGet(relativePath, queryParameter, new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// try {
	// onResponse.accept(parseJsonList(clazz, response.body().byteStream()));
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// private Response internalGet(String relativePath, Map<String, String> queryParameter, Callback callback) throws IOException {
	// Request request = new Request.Builder().url(buildHttpUrl(relativePath, queryParameter)).get().build();
	// if (callback == null) {
	// return mClient.newCall(request).execute();
	// } else {
	// mClient.newCall(request).enqueue(callback);
	// return null;
	// }
	// }
	//
	// @Override
	// public <C extends PostContent> String post(String relativePath, Map<String, String> queryParameter, C content) throws IOException {
	// Response response = internalPost(relativePath, queryParameter, MediaType.parse(content.contentType()), content.content(), null);
	// try {
	// return response.isSuccessful() ? response.body().string() : null;
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T, C extends PostContent> VDEResponse<T> post(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content)
	// throws IOException {
	// Response response = internalPost(relativePath, queryParameter, MediaType.parse(content.contentType()), content.content(), null);
	// try {
	// return parseJson(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T, C extends PostContent> void post(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content,
	// Consumer<VDEResponse<T>> onResponse, Consumer<IOException> onFailure) throws IOException {
	// internalPost(relativePath, queryParameter, MediaType.parse(content.contentType()), content.content(), new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// try {
	// onResponse.accept(parseJson(clazz, response.body().byteStream()));
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// @Override
	// public <T, C extends PostContent> VDEResponse<List<T>> postList(Class<T> clazz, String relativePath, Map<String, String> queryParameter,
	// C content) throws IOException {
	// Response response = internalPost(relativePath, queryParameter, MediaType.parse(content.contentType()), content.content(), null);
	// try {
	// return parseJsonList(clazz, response.body().byteStream());
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public <T, C extends PostContent> void postList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content,
	// Consumer<VDEResponse<List<T>>> onResponse, Consumer<IOException> onFailure) throws IOException {
	// internalPost(relativePath, queryParameter, MediaType.parse(content.contentType()), content.content(), new Callback() {
	// @Override
	// public void onResponse(Response response) throws IOException {
	// try {
	// onResponse.accept(parseJsonList(clazz, response.body().byteStream()));
	// } finally {
	// response.body().close();
	// }
	// }
	//
	// @Override
	// public void onFailure(Request request, IOException e) {
	// onFailure.accept(e);
	// }
	// });
	// }
	//
	// private Response internalPost(String relativePath, Map<String, String> queryParameter, MediaType contentType, String content, Callback
	// callback)
	// throws IOException {
	// RequestBody body = RequestBody.create(contentType, content);
	// Request request = new Request.Builder().url(buildHttpUrl(relativePath, queryParameter)).post(body).build();
	// if (callback == null) {
	// return mClient.newCall(request).execute();
	// } else {
	// mClient.newCall(request).enqueue(callback);
	// return null;
	// }
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public synchronized <T> VDEResponse<T> parseJson(Class<T> clazz, String toParse) {
	// if (toParse == null || toParse.isEmpty())
	// return null;
	// try {
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, clazz);
	// VDEResponse<T> response = (VDEResponse<T>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public <T> VDEResponse<T> parseJson(Class<T> clazz, InputStream toParse) {
	// if (toParse == null)
	// return null;
	// try {
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, clazz);
	// VDEResponse<T> response = (VDEResponse<T>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public <T> VDEResponse<T> parseJson(Class<T> clazz, Reader toParse) {
	// if (toParse == null)
	// return null;
	// try {
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, clazz);
	// VDEResponse<T> response = (VDEResponse<T>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public synchronized <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, String toParse) {
	// if (toParse == null || toParse.isEmpty())
	// return null;
	// try {
	// CollectionType collectionType = mMapper.getTypeFactory().constructCollectionType(List.class, clazz);
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, collectionType);
	// VDEResponse<List<T>> response = (VDEResponse<List<T>>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public synchronized <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, InputStream toParse) {
	// if (toParse == null)
	// return null;
	// try {
	// CollectionType collectionType = mMapper.getTypeFactory().constructCollectionType(List.class, clazz);
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, collectionType);
	// VDEResponse<List<T>> response = (VDEResponse<List<T>>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public synchronized <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, Reader toParse) {
	// if (toParse == null)
	// return null;
	// try {
	// CollectionType collectionType = mMapper.getTypeFactory().constructCollectionType(List.class, clazz);
	// JavaType type = mMapper.getTypeFactory().constructParametrizedType(VDEResponse.class, VDEResponse.class, collectionType);
	// VDEResponse<List<T>> response = (VDEResponse<List<T>>) mMapper.readValue(toParse, type);
	// return response;
	// } catch (IOException e) {
	// LOG.error("Error while parsing String '{}'", toParse);
	// LOG.error(e);
	// }
	// return null;
	// }
	
	/**
	 * This is how it was intended to be!
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized <T> T parseJson(String toParse, Class... clazz) {
		if (toParse == null || toParse.isEmpty() || clazz == null || clazz.length == 0)
			return null;
		try {
			JavaType type = null;
			for (int i = clazz.length - 1; i >= 0; i--) {
				JavaType t = null;
				if (Collection.class.isAssignableFrom(clazz[i])) {
					if (type == null)
						t = mMapper.getTypeFactory().constructRawCollectionType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructCollectionType(clazz[i], type);
				} else {
					if (type == null)
						t = mMapper.getTypeFactory().constructType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructParametrizedType(clazz[i], clazz[i], type);
				}
				type = t;
				LOG.trace(clazz[i] + " --> " + type);
			}
			return mMapper.readValue(toParse, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized <T> T parseJson(InputStream toParse, Class... clazz) {
		if (toParse == null || clazz == null || clazz.length == 0)
			return null;
		try {
			JavaType type = null;
			for (int i = clazz.length - 1; i >= 0; i--) {
				JavaType t = null;
				if (Collection.class.isAssignableFrom(clazz[i])) {
					if (type == null)
						t = mMapper.getTypeFactory().constructRawCollectionType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructCollectionType(clazz[i], type);
				} else {
					if (type == null)
						t = mMapper.getTypeFactory().constructType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructParametrizedType(clazz[i], clazz[i], type);
				}
				type = t;
				LOG.trace(clazz[i] + " --> " + type);
			}
			return mMapper.readValue(toParse, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized <T> T parseJson(Reader toParse, Class... clazz) {
		if (toParse == null || clazz == null || clazz.length == 0)
			return null;
		try {
			JavaType type = null;
			for (int i = clazz.length - 1; i >= 0; i--) {
				JavaType t = null;
				if (Collection.class.isAssignableFrom(clazz[i])) {
					if (type == null)
						t = mMapper.getTypeFactory().constructRawCollectionType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructCollectionType(clazz[i], type);
				} else {
					if (type == null)
						t = mMapper.getTypeFactory().constructType(clazz[i]);
					else
						t = mMapper.getTypeFactory().constructParametrizedType(clazz[i], clazz[i], type);
				}
				type = t;
				LOG.trace(clazz[i] + " --> " + type);
			}
			return mMapper.readValue(toParse, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public <T> String toJsonString(T content) {
		try {
			return mMapper.writeValueAsString(content);
		} catch (JsonProcessingException e) {
			LOG.error("Error while creating String '{}'", content);
		}
		return null;
	}
	
	@PreDestroy
	private void preDestroy() {
		LOG.debug("PreDestroy: tear down " + getClass().getName());
		mClient = null;
		mMapper = null;
	}
}
