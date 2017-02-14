package eu.dzim.poc.fx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Status Codes from https://en.wikipedia.org/wiki/List_of_HTTP_status_codes<br>
 * TODO: https://en.wikipedia.org/wiki/List_of_FTP_server_return_codes<br>
 * <br>
 * Be aware: The unofficial codes may be used with different meanings!
 * 
 * 
 * @author daniel.zimmermann@cnlab.ch
 * @see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 */
public enum HttpStatus {
	
	// -------------------------------------------------------------------------
	// 1xx Informational
	// -------------------------------------------------------------------------
	CONTINUE(100, "Continue"),
	//
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	//
	PROCESSING(102, "Processing", "WebDAV; RFC 2518"),
	//
	CHECKPOINT(103, "checkpoint"),
	
	// -------------------------------------------------------------------------
	// 2xx Success
	// -------------------------------------------------------------------------
	OK(200, "OK"),
	//
	CREATED(201, "Created"),
	//
	ACCEPTED(202, "Accepted"),
	//
	NON_AUTHORATIVE_INFORMATION(203, "Non-Authoritative Information", "since HTTP/1.1"),
	//
	NO_CONTENT(204, "No Content"),
	//
	RESET_CONTENT(205, "Reset Content"),
	//
	PARTIAL_CONTENT(206, "Partial Content", "RFC 7233"),
	//
	MULTI_STATUS(207, "Multi-Status", "WebDAV; RFC 4918"),
	//
	ALREADY_REPORTED(208, "Already Reported", "WebDAV; RFC 5842"),
	//
	IM_USED(226, "IM Used", "RFC 3229"),
	
	// -------------------------------------------------------------------------
	// 3xx Redirection
	// -------------------------------------------------------------------------
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	//
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	//
	FOUND(302, "Found"),
	//
	SEE_OTHER(303, "See Other", "since HTTP/1.1"),
	//
	NOT_MODIFIED(304, "Not Modified", "RFC 7232"),
	//
	USE_PROXY(305, "Use Proxy", "since HTTP/1.1"),
	//
	SWITCH_PROXY(306, "Switch Proxy"),
	//
	TEMPORARY_REDIRECT(307, "Temporary Redirect", "since HTTP/1.1"),
	//
	PERMANENT_REDIRECT(308, "Permanent Redirect", "RFC 7538"),
	
	// -------------------------------------------------------------------------
	// 4xx Client Error
	// -------------------------------------------------------------------------
	BAD_REQUEST(400, "Bad Request"),
	//
	UNAUTHORIZED(401, "Unauthorized", "RFC 7235"),
	//
	PAYMENT_REQUIRED(402, "Payment Required"),
	//
	FORBIDDEN(403, "Forbidden"),
	//
	NOT_FOUND(404, "Not Found"),
	//
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	//
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	//
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", "RFC 7235"),
	//
	REQUEST_TIMEOUT(408, "Request Timeout"),
	//
	CONFLICT(409, "Conflict"),
	//
	GONE(410, "Gone"),
	//
	LENGTH_REQUIRED(411, "Length Required"),
	//
	PRECONDITION_FAILED(412, "Precondition Failed", "RFC 7232"),
	//
	PAYLOAD_TOO_LARGE(413, "Payload Too Large", "RFC 7231"),
	//
	URI_TOO_LONG(414, "URI Too Long", "RFC 7231"),
	//
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	//
	RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable", "RFC 7233"),
	//
	EXPECTATION_FAILED(417, "Expectation Failed"),
	//
	IM_A_TEAPOT(418, "I'm a teapot", "RFC 2324"),
	//
	AUTHENTICATION_TIMEOUT(419, "Authentication Timeout", "not in RFC 2616"),
	//
	MISDIRECTED_REQUEST(421, "Misdirected Request", "RFC 7540"),
	//
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "WebDAV; RFC 4918"),
	//
	LOCKED(423, "Locked", "WebDAV; RFC 4918"),
	//
	FAILED_DEPENDENCY(424, "Failed Dependency", "WebDAV; RFC 4918"),
	//
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	//
	PRECONDITION_REQUIRED(428, "Precondition Required", "RFC 6585"),
	//
	TOO_MANY_REQUESTS(429, "Too Many Requests", "RFC 6585"),
	//
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large", "RFC 6585"),
	//
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons", "Internet draft"),
	
	// -------------------------------------------------------------------------
	// 5xx Server Error
	// -------------------------------------------------------------------------
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	//
	NOT_IMPLEMENTED(501, "Not Implemented"),
	//
	BAD_GATEWAY(502, "Bad Gateway"),
	//
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	//
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	//
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
	//
	VARIANT_ALSO_NEGOTIATED(506, "Variant Also Negotiates", "RFC 2295"),
	//
	INSUFFICIENT_STORAGE(507, "Insufficient Storage", "WebDAV; RFC 4918"),
	//
	LOOP_DETECTED(508, "Loop Detected", "WebDAV; RFC 5842"),
	//
	NOT_EXTENDED(510, "Not Extended", "RFC 2774"),
	//
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required", "RFC 6585"),
	
	// -------------------------------------------------------------------------
	// Unofficial codes
	// -------------------------------------------------------------------------
	
	// RESTful error respones
	METHOD_FAILURE(420, "Method Failure", "Spring Framework"),
	//
	ENHANCE_YOUR_CALM(420, "Enhance Your Calm", "Twitter, later: 429/TOO_MANY_REQUESTS"),
	//
	BLOCKED_BY_WINDOWS_PARENTAL_CONTROLS(450, "Blocked by Windows Parental Controls", "Microsoft"),
	//
	INVALID_TOKEN(498, "Invalid Token", "Esri"),
	//
	TOKEN_REQUIRED(499, "Token Required", "Esri"),
	//
	BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded", "Apache Web Server/cPanel"),
	
	// Internet Information Services
	LOGIN_TIMEOUT(440, "Login Timeout"),
	//
	RETRY_WITH(449, "Retry With"),
	//
	REDIRECT(451, "Redirect", "M$ Exchange ActiveSync to re-run HTTP Autodiscovery"),
	
	// nginx
	NO_RESPONSE(444, "No Response"),
	//
	SSL_CERTIFICATE_ERROR(495, "SSL Certificate Error", "on invalid client vertificate"),
	//
	SSL_CERTIFICATE_REQUIRED(496, "SSL Certificate Required"),
	//
	HTTP_REQUEST_SENT_TO_HTTPS_PORT(497, "HTTP Request Sent to HTTPS Port", "HTTP attempt on HTTPS port"),
	//
	CLIENT_CLOSED_REQUEST(499, "Client Closed Request"),
	
	// CloudFlare
	UNKNOWN_ERROR(520, "Unknown Error"),
	//
	WEB_SERVER_IS_DOWN(521, "Web Server Is Down"),
	//
	CONNECTION_TIMEOUT(522, "Connection Timed Out"),
	//
	ORIGIN_IS_UNREACHABLE(523, "Origin Is Unreachable"),
	//
	A_TIMEOUT_OCCURED(524, "A Timeout Occurred"),
	//
	SSL_HANDSHAKE_FAILED(525, "SSL Handshake Failed"),
	//
	INVALID_SSL_CERTIFICATE(526, "Invalid SSL Certificate"),
	
	// -------------------------------------------------------------------------
	// END
	// -------------------------------------------------------------------------
	;
	
	public static final HttpStatus[] INFORMATIONAL_CODES = new HttpStatus[] { CONTINUE, SWITCHING_PROTOCOLS, PROCESSING, CHECKPOINT };
	public static final HttpStatus[] SUCCESS_CODES = new HttpStatus[] { OK, CREATED, ACCEPTED, NON_AUTHORATIVE_INFORMATION, NO_CONTENT, RESET_CONTENT,
			PARTIAL_CONTENT, MULTI_STATUS, ALREADY_REPORTED, IM_USED };
	public static final HttpStatus[] REDIRECTION_CODES = new HttpStatus[] { MULTIPLE_CHOICES, MOVED_PERMANENTLY, FOUND, SEE_OTHER, NOT_MODIFIED,
			USE_PROXY, SWITCH_PROXY, TEMPORARY_REDIRECT, PERMANENT_REDIRECT };
	public static final HttpStatus[] CLIENT_ERROR_CODES = new HttpStatus[] { BAD_REQUEST, UNAUTHORIZED, PAYMENT_REQUIRED, FORBIDDEN, NOT_FOUND,
			METHOD_NOT_ALLOWED, NOT_ACCEPTABLE, PROXY_AUTHENTICATION_REQUIRED, REQUEST_TIMEOUT, CONFLICT, GONE, LENGTH_REQUIRED, PRECONDITION_FAILED,
			PAYLOAD_TOO_LARGE, URI_TOO_LONG, UNSUPPORTED_MEDIA_TYPE, RANGE_NOT_SATISFIABLE, EXPECTATION_FAILED, IM_A_TEAPOT, AUTHENTICATION_TIMEOUT,
			MISDIRECTED_REQUEST, UNPROCESSABLE_ENTITY, LOCKED, FAILED_DEPENDENCY, UPGRADE_REQUIRED, PRECONDITION_REQUIRED, TOO_MANY_REQUESTS,
			REQUEST_HEADER_FIELDS_TOO_LARGE, UNAVAILABLE_FOR_LEGAL_REASONS };
	public static final HttpStatus[] SERVER_ERROR_CODES = new HttpStatus[] { INTERNAL_SERVER_ERROR, NOT_IMPLEMENTED, BAD_GATEWAY, SERVICE_UNAVAILABLE,
			GATEWAY_TIMEOUT, HTTP_VERSION_NOT_SUPPORTED, VARIANT_ALSO_NEGOTIATED, INSUFFICIENT_STORAGE, LOOP_DETECTED, NOT_EXTENDED,
			NETWORK_AUTHENTICATION_REQUIRED };
			
	private final int code;
	private final String text;
	private final String note;
	
	private HttpStatus(final int code, final String text) {
		this.code = code;
		this.text = text;
		this.note = null;
	}
	
	private HttpStatus(final int code, final String text, final String note) {
		this.code = code;
		this.text = text;
		this.note = note;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getText() {
		return text;
	}
	
	public String getNote() {
		return note;
	}
	
	private static final Map<Integer, HttpStatus> INTERNAL_CODE_MAP = new HashMap<>();
	
	public static HttpStatus fromCode(final int code) {
		if (code < 100)
			return null;
		HttpStatus status = INTERNAL_CODE_MAP.get(code);
		if (status != null)
			return status;
		for (HttpStatus _status : values()) {
			if (_status.code == code) {
				INTERNAL_CODE_MAP.put(code, _status);
				return _status;
			}
		}
		return null;
	}
}
