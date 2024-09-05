package com.example.chat_anthropic.data

class ChatRepository(private val chatDao: ChatDao, private val messageDao: MessageDao) {

    fun getAllChats() = chatDao.getAllChats()

    suspend fun createNewChat(title: String): String {
        val chat = Chat(title = title)
        chatDao.insertChat(chat)
        return chat.id
    }

    suspend fun deleteChat(chat: Chat) {
        chatDao.deleteChat(chat)
        messageDao.deleteMessagesForChat(chat.id)
    }

    fun getMessagesForChat(chatId: String) = messageDao.getMessagesForChat(chatId)

    suspend fun addMessage(chatId: String, content: String, isFromUser: Boolean) {
        val message = Message(chatId = chatId, content = content, isFromUser = isFromUser)
        messageDao.insertMessage(message)
    }

}