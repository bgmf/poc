package eu.dzim.poc.fx.service;

import java.io.InputStream;
import java.io.Reader;

public interface RestService {
	
	// /**
	// * POST a form to the server. You can specify both query parameter and formParameter.
	// *
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// * @param formParameter
	// * the {@link Map} of form parameter to POST
	// *
	// * @return either a {@link String} with the response, or <code>null</code>, if the call was not successful
	// *
	// * @throws IOException
	// * on any connection problem
	// */
	// String postForm(String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter) throws IOException;
	//
	// /**
	// * POST a form to the server and assume, that the response is in JSON format. In this case the response content will be mapped to the specified
	// * {@link Class}. You can specify both query parameter and formParameter.
	// *
	// * @param clazz
	// * the {@link Class} to try to map the response to
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// * @param formParameter
	// * the {@link Map} of form parameter to POST
	// *
	// * @return either an instance of the specified {@link Class} parameter, or <code>null</code>
	// *
	// * @throws IOException
	// * on any connection problem, or when we could not parse the response
	// */
	// <T> VDEResponse<T> postForm(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter)
	// throws IOException;
	//
	// <T> VDEResponse<List<T>> postFormList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String>
	// formParameter)
	// throws IOException;
	//
	// <T> void postForm(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter,
	// Consumer<VDEResponse<T>> onResponse, Consumer<IOException> onFailure) throws IOException;
	//
	// <T> void postFormList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Map<String, String> formParameter,
	// Consumer<VDEResponse<List<T>>> onResponse, Consumer<IOException> onFailure) throws IOException;
	//
	// /**
	// * GET a {@link String} back from a server and assume, that the response is in JSON format. In this case the response content will be mapped to
	// * the specified {@link Class}. You can specify both query parameter and formParameter.
	// *
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// *
	// * @return either a {@link String} with the response, or <code>null</code>, if the call was not successful
	// *
	// * @throws IOException
	// * on any connection problem
	// */
	// String get(String relativePath, Map<String, String> queryParameter) throws IOException;
	//
	// /**
	// * GET a {@link String} back from a server. You can specify both query parameter and formParameter.
	// *
	// * @param clazz
	// * the {@link Class} to try to map the response to
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// *
	// * @return either an instance of the specified {@link Class} parameter, or <code>null</code>
	// *
	// * @throws IOException
	// * on any connection problem, or when we could not parse the response
	// */
	// <T> VDEResponse<T> get(Class<T> clazz, String relativePath, Map<String, String> queryParameter) throws IOException;
	//
	// <T> VDEResponse<List<T>> getList(Class<T> clazz, String relativePath, Map<String, String> queryParameter) throws IOException;
	//
	// <T> void get(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Consumer<VDEResponse<T>> onResponse,
	// Consumer<IOException> onFailure) throws IOException;
	//
	// <T> void getList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, Consumer<VDEResponse<List<T>>> onResponse,
	// Consumer<IOException> onFailure) throws IOException;
	//
	// /**
	// *
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// * @param content
	// * cannot be <code>null</code>
	// *
	// * @return either a {@link String} with the response, or <code>null</code>, if the call was not successful
	// *
	// * @throws IOException
	// * on any connection problem
	// */
	// <C extends PostContent> String post(String relativePath, Map<String, String> queryParameter, C content) throws IOException;
	//
	// /**
	// *
	// * @param clazz
	// * the {@link Class} to try to map the response to
	// * @param relativePath
	// * the relative path to the target - you don't need to take care of the base path
	// * @param queryParameter
	// * the {@link Map} of query parameter
	// * @param content
	// * cannot be <code>null</code>
	// *
	// * @return either an instance of the specified {@link Class} parameter, or <code>null</code>
	// *
	// * @throws IOException
	// * on any connection problem, or when we could not parse the response
	// */
	// <T, C extends PostContent> VDEResponse<T> post(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content)
	// throws IOException;
	//
	// <T, C extends PostContent> VDEResponse<List<T>> postList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content)
	// throws IOException;
	//
	// <T, C extends PostContent> void post(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content,
	// Consumer<VDEResponse<T>> onResponse, Consumer<IOException> onFailure) throws IOException;
	//
	// <T, C extends PostContent> void postList(Class<T> clazz, String relativePath, Map<String, String> queryParameter, C content,
	// Consumer<VDEResponse<List<T>>> onResponse, Consumer<IOException> onFailure) throws IOException;
	//
	// /*
	// * first generic approach for parsing
	// */
	//
	// <T> VDEResponse<T> parseJson(Class<T> clazz, String toParse);
	//
	// <T> VDEResponse<T> parseJson(Class<T> clazz, InputStream toParse);
	//
	// <T> VDEResponse<T> parseJson(Class<T> clazz, Reader toParse);
	//
	// <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, String toParse);
	//
	// <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, InputStream toParse);
	//
	// <T> VDEResponse<List<T>> parseJsonList(Class<T> clazz, Reader toParse);
	
	 /*
	 * The way the parser schould have looked from the beginning: VDEResponse<List<User>> you'd call #parseJson(<toParse>, VDEResponse, List, User);
	 */
	 @SuppressWarnings("rawtypes")
	 <T> T parseJson(String toParse, Class... clazz);
	
	 @SuppressWarnings("rawtypes")
	 <T> T parseJson(InputStream toParse, Class... clazz);
	
	 @SuppressWarnings("rawtypes")
	 <T> T parseJson(Reader toParse, Class... clazz);
	
	/*
	 * toString method
	 */
	<T> String toJsonString(T content);
}
