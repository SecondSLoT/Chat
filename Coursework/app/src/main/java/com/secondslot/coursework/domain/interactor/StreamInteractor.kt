package com.secondslot.coursework.domain.interactor

import com.secondslot.coursework.data.api.model.ServerResult
import com.secondslot.coursework.domain.model.Message
import com.secondslot.coursework.domain.model.Stream
import com.secondslot.coursework.domain.usecase.message.DeleteMessageUseCase
import com.secondslot.coursework.domain.usecase.message.EditMessageUseCase
import com.secondslot.coursework.domain.usecase.message.GetMessagesUseCase
import com.secondslot.coursework.domain.usecase.message.MoveMessageUseCase
import com.secondslot.coursework.domain.usecase.message.SendMessageUseCase
import com.secondslot.coursework.domain.usecase.stream.CreateOrSubscribeOnStreamUseCase
import com.secondslot.coursework.domain.usecase.stream.GetAllStreamsUseCase
import com.secondslot.coursework.domain.usecase.stream.GetStreamByIdUseCase
import com.secondslot.coursework.domain.usecase.stream.GetSubscribedStreamsUseCase
import com.secondslot.coursework.domain.usecase.stream.GetTopicsUseCase
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class StreamInteractor @Inject constructor(
    private val createOrSubscribeOnStreamUseCase: CreateOrSubscribeOnStreamUseCase,
    private val getAllStreamsUseCase: GetAllStreamsUseCase,
    private val getStreamByIdUseCase: GetStreamByIdUseCase,
    private val getSubscribedStreamsUseCase: GetSubscribedStreamsUseCase,
    private val getTopicsUseCase: GetTopicsUseCase
) {

    fun createOrSubscribeOnStream(
        subscriptions: Map<String, Any>,
        announce: Boolean = false
    ): Single<ServerResult> {
        return createOrSubscribeOnStreamUseCase.execute(subscriptions, announce)
    }

    fun getAllStreams(searchQuery: String = ""): Observable<List<Stream>> {
        return getAllStreamsUseCase.execute(searchQuery)
    }

    fun getStreamById(streamId: Int): Observable<Stream> {
        return getStreamByIdUseCase.execute(streamId)
    }

    fun getSubscribedStreams(searchQuery: String = ""): Observable<List<Stream>> {
        return getSubscribedStreamsUseCase.execute(searchQuery)
    }

    fun getTopics(streamId: Int): Observable<List<Stream.Topic>> {
        return getTopicsUseCase.execute(streamId)
    }
}
