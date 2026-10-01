package com.example.tiketbantu.data.repository

import com.example.tiketbantu.data.local.dao.TicketWithMeta
import com.example.tiketbantu.data.local.entity.TicketEntity
import com.example.tiketbantu.domain.model.Ticket

fun TicketWithMeta.toDomain(): Ticket = Ticket(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    categoryName = categoryName,
    locationBuilding = locationBuilding,
    locationFloor = locationFloor,
    locationRoom = locationRoom,
    status = status,
    imageUrl = imageUrl,
    reporterId = reporterId,
    reporterName = reporterName,
    agentId = agentId,
    agentName = agentName,
    supportCount = supportCount,
    isSupportedByMe = isSupportedByMe,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Only persisted columns; names and support aggregates are derived by joins. */
fun Ticket.toEntity(): TicketEntity = TicketEntity(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    locationBuilding = locationBuilding,
    locationFloor = locationFloor,
    locationRoom = locationRoom,
    status = status,
    imageUrl = imageUrl,
    reporterId = reporterId,
    agentId = agentId,
    createdAt = createdAt,
    updatedAt = updatedAt
)
