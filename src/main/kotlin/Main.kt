package org

import org.services.Gateway
import org.services.NotificationServiceImpl

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val service = NotificationServiceImpl(Gateway())
    service.send("news", "user", "news 1")
    service.send("news", "user", "news 2")
    service.send("news", "user", "news 3")
    service.send("news", "another user", "news 1")
    service.send("update", "user", "update 1")
}