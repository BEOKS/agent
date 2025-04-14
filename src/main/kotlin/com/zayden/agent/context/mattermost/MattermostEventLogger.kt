package com.zayden.agent.context.mattermost

import com.zayden.agent.logger
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.stereotype.Component


@Component
class MattermostEventLogger : Subscriber<MattermostEvent> {

    val log=logger()

    override fun onSubscribe(s: Subscription?) {
        log.info(s.toString())
    }

    override fun onError(t: Throwable?) {
        log.error(t.toString())
    }

    override fun onComplete() {
        log.info("onComplete")
    }

    override fun onNext(t: MattermostEvent?) {
        log.info(t.toString())
    }
}
