package com.neilalexander.jnacl.crypto.hashblocks;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

public class sha256Test {

  @Test
  public void load_and_store() {
    byte[] buf = new byte[4];

    sha256.store_bigendian(buf, 0, 0x10ddee40);
    int i = sha256.load_bigendian(buf, 0);

    Assertions.assertThat(i).isEqualTo(0x10ddee40);
  }

  @Test
  public void load() {
    byte[] buf = new byte[]{16, (byte) 0xdd, (byte) 0xee, 64};

    int i = sha256.load_bigendian(buf, 0);

    Assertions.assertThat(i).isEqualTo(0x10ddee40);
  }

  @Test
  public void store() {
    byte[] buf = new byte[4];

    sha256.store_bigendian(buf, 0, 0x10ddee40);

    Assertions.assertThat(buf).isEqualTo(new byte[]{16, (byte) 0xdd, (byte) 0xee, 64});
  }

}