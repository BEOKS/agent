package com.zayden.agent.util

import org.junit.jupiter.api.Test

class TotpGeneratorTest{
  private val totpGenerator = TotpGenerator()

  @Test
  fun test(){
   println(totpGenerator.generate("MCRA6JSTJKTEYRM3DPE2ORLCSIY6TMJW"))
  }
}
