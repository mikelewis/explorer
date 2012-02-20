FetchedUrl -> LoadBalancer -> System

LoadBalancer:
  Balances all the Systems.
  ConsistentHash Load Balancer.


System:
  Handles a slice of all the hosts.

  SystemReceiver -> Process HTML -> URL Processor -> URL Scheduler

  SystemReceiver:
    What the load balancer passes messages to.
    Notifies URL Scheduler that the url has been processed so the URL Scheduler can do more work on that host.

  Process HTML:
    Parses DOM.
    Calls callbacks (these callbacks can be anything, indexes, stats etc etc).
    Passes URLs found in DOM to URL Processor.

  URL Processor:
    Removes already seen urls.
    Makes sure we have a robots.txt for that host.
    Removes all urls that dont meet robots.txt
    Passes urls along with Crawl-Delay to URL Scheduler.

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
