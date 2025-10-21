package com.arttttt.calenda.feature.eventdetails.di

import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.eventdetails.domain.EventDetailsStore
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(EventDetailsScreenScope::class)
interface EventDetailsScreenGraph {

    @ContributesTo(UIScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createEventDetailsScreenGraph(
            @Provides eventId: Long,
        ): EventDetailsScreenGraph
    }

    val eventDetailsStore: EventDetailsStore

    @Provides
    @SingleIn(EventDetailsScreenScope::class)
    fun provideEventDetailsStore(
        eventDetailsStoreFactory: EventDetailsStore.Factory,
        eventId: Long,
    ): EventDetailsStore {
        return eventDetailsStoreFactory.create(eventId)
    }
}
