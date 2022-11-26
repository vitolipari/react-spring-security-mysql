package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ECDHKeyPack {

	private String privateKey;
	private String publicKey;
	private String externalKey;
	private String sharedKey;

	public Map<String, String> toMap() {
		return new HashMap<String, String>() {{
			put("privateKey", privateKey);
			put("publicKey", publicKey);
			put("externalKey", externalKey);
			put("sharedKey", sharedKey);
		}};
	}

}
