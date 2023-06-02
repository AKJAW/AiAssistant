package com.akjaw.ai.assistant.shared.gpt

private val AddTask = """
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