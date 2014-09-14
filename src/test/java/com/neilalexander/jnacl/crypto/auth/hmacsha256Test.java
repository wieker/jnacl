package com.neilalexander.jnacl.crypto.auth;

import org.testng.annotations.Test;

import static com.neilalexander.jnacl.NaCl.asHex;
import static org.fest.assertions.Assertions.assertThat;

public class hmacsha256Test {

  private static byte[] secret  = "0cba66066896ffb51e92bc3c36ffa627".getBytes();
  private static byte[] message = "a secret message".getBytes();

  @Test
  public void happy_path() throws Exception {
    byte[] buf = new byte[32];
    hmacsha256.crypto_auth(buf, message, message.length, secret);

    assertThat(asHex(buf)).isEqualTo("04E1A5D1EDD8585C7C5ACD6E487F336A8ED50DE2DDB6946DAD8EE26BCE6DD54C");
  }
}