ScheduledUrls(queue) -> Load Balancer -> Url Scheduler

Load Balancer:
  Consistent Hash based on host.
  Distributes amongst url schedulers.

URL Scheduler:
  Contains seperate queues for each host in redis.
  When we dequeue a url, we send it to the FetchURLQueue (RabbitMQ in this example).
  In order to dequeue a url:
    If queue is empty ->
      send it to FetchURLQUeue
      set as currently processing for host.
    else if queue is not empty:
      Enqueue url in the specific queue for that host.
  When notified that a url has been processed (from SystemReceiver), will schedule a dequeue of that host's queue for X seconds where X is:
    If Crawl-Delay has been set on robots.txt, use that delay. Otherwise use default (say 3 seconds).
