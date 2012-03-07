Consumer of FetchedUrlQueue.

To scale, just add more consumers to FetchedUrlQueue.


FetchedUrl -> System

LoadBalancer:
  Balances all the Systems.
  ConsistentHash Load Balancer.


System:
  Handles a slice of all the hosts.

  SystemReceiver -> Process HTML -> URL Processor -> Enqueue to URLScheduler

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
