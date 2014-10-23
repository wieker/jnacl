package com.neilalexander.jnacl.crypto.auth;

import org.testng.annotations.Test;

import static com.neilalexander.jnacl.NaCl.asHex;
import static org.fest.assertions.Assertions.assertThat;

public class crypto_hashblocks_sha256Test {

  @Test
  public void load_and_store() {
    byte[] buf = new byte[4];

    crypto_hashblocks_sha256.store_bigendian(buf, 0, 0x10ddee40);
    int i = crypto_hashblocks_sha256.load_bigendian(buf, 0);

    assertThat(i).isEqualTo(0x10ddee40);
  }

  @Test
  public void load() {
    byte[] buf = new byte[]{16, (byte) 0xdd, (byte) 0xee, 64};

    int i = crypto_hashblocks_sha256.load_bigendian(buf, 0);

    assertThat(i).isEqualTo(0x10ddee40);
  }

  @Test
  public void store() {
    byte[] buf = new byte[4];

    crypto_hashblocks_sha256.store_bigendian(buf, 0, 0x10ddee40);

    assertThat(buf).isEqualTo(new byte[]{16, (byte) 0xdd, (byte) 0xee, 64});
  }

}