package com.liparistudios.reactspringsecmysql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ECDHKeyPack {

	private String privateKey;
	private String publicKey;
	private String externalKey;
	private String sharedKey;

}
