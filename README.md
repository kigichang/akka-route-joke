# Akka Route Joke

延用 __吃飯睡覺打東東__ 的企鵝笑話，來 demo Akka Route 的功能。每隻企鵝會記錄被打了幾次。

名叫 __東東__ 的企鵝，在被打第 __4__ 次時，會說 __不要打擾我__，在第 6 次時，會 __爆炸__，在超過第 __6__ 次後，牠就成仙了。記者在啟動後，會對企鵝群打 100 次。


## 說明

本程式，主要是由 `Reporter` 來產生 `Penguin`，也因此 `Reporter` 是 `Penguin` 的 supervisor。各位可以在 `Reporter` 的程式碼中，修改

```
override val supervisorStrategy = 
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    //AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _ : DontBotherMeException => Resume
    case _ : ExplodeException => Restart
    case _ : IamGodException => Stop
    case _ : Exception => Escalate
    }
```

或註解掉這一段程式，來了解 Akka Stragegy 的運作方式。

Akka Stragey 預設是 `OneForOneStrategy`，在發現有 `Exception` 時，會自動重啟 Child Actor.
