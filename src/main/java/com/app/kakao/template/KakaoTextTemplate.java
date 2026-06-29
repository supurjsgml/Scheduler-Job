package com.app.kakao.template;

import java.util.List;

public record KakaoTextTemplate(String object_type, String text, Link link, List<Button> buttons) {
	
	public static KakaoTextTemplate restartTemplate(String msg, String serverUrl) {
		return restartTemplate(msg, serverUrl, "/api/lightsail/restart");
	}

	public static KakaoTextTemplate restartTemplate(String msg, String serverUrl, String restartPath) {
		return new KakaoTextTemplate("text", "[에러 발생] : ".concat(msg), new Link(serverUrl)
								   , List.of(new Button("서버 재시작 가쥬아", new Link(serverUrl.concat(restartPath)))));
	}

	public record Link(String web_url, String mobile_web_url) {
		public Link(String url) {
			this(url, url);
		}
	}

	public record Button(String title, Link link) {
	}
}