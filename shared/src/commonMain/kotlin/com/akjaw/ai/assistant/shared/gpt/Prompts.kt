package com.akjaw.ai.assistant.shared.gpt

private val AddNotionTask = """
Based on {task} return single-line JSON and nothing more in the following format: {"date": "YYYY-MM-DD HH:mm", "priority": "important urgent|normal", "category": "work", "name": "Concise yet descriptive task name"}. It's important for you to speak only in JSON, because your answer will be directly parsed by app. Note: if date is not provided, set it for current day 13:00.

### Today's date you have to use
{{formatDate(now; "ddd")}} {{formatDate(now; "YYYY-MM-DD HH:mm")}}

### categories
me: ai devs, ai app, all others
work: checking something, reading PR, Code Review, Programming, Ticket, email
blog: writing articles, creating hero images, updating blog theme, public speaking

### task
{{1.task}}
""".trimIndent()

object TickTick {


//    title
//    dueDate Due date and time in "yyyy-MM-dd'T'HH:mm:ssZ" format Example : "2019-11-13T03:00:00+0000"
//    reminders Lists of reminders specific to the task Example : [ "TRIGGER:P0DT9H0M0S", "TRIGGER:PT0S" ]
//    priority The priority of task, default is "0" Value : None:0, Low:1, Medium:3, High5
//    projectId
private val AddTask = """
Based on {task} return single-line JSON and nothing more in the following format: {"date": "yyyy-MM-ddTHH:mm:ss", "priority": "high|normal|low", "category": "me", "name": "Concise yet descriptive task name"}. It's important for you to speak only in JSON, because your answer will be directly parsed by app. Note: pay special attention to the time which might be just a number, also if date is not provided, set it for current day 13:00.

### Today's date you have to use
{{formatDate(now; "ddd")}} {{formatDate(now; "YYYY-MM-DD HH:mm")}}

### categories
me: all others
doctors: medical appointments
house: house chores, yard work
work: FootballCo, recording courses, mailingr
learning: reading articles, watching courses
blog: writing articles, creating hero images, updating blog theme, public speaking

### task
{{1.task}}
""".trimIndent()

    private val testTasks = """
Next Monday at 16:00 Meeting with Friends
Tomorrow at 9:00 Select a topic for next article
Important Cut down trees tommorow
Dentist visit 16.06 at 17:00
low priority doctors appointment tommorow at 9:00
Reconrd Android next level lesson
Read android article
14 czerwca 7:05 wycieczka Kartagina
""".trimIndent()

}


// {"id":"647db5118f08b956daf3d75e","projectId":"603130609d5a1eb78d7ba98c","sortOrder":-53051436040192,"title":"Select topic for next article","startDate":"2023-06-06T07:00:00.000+0000","dueDate":"2023-06-06T07:00:00.000+0000","timeZone":"Europe/Warsaw","isAllDay":true,"priority":3,"status":0,"tags":[]}
// {"id":"647db5118f08b956daf3d75e","projectId":"603130609d5a1eb78d7ba98c","sortOrder":-53051436040192,"title":"Select topic for next article","desc":"","startDate":"2023-06-06T07:00:00.000+0000","dueDate":"2023-06-06T07:00:00.000+0000","timeZone":"Europe/Warsaw","isAllDay":false,"priority":3,"reminders":["TRIGGER:PT0S"],"status":0,"columnId":""}