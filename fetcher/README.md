Consumer of FetchURLQueue.

Fetcher has many URLWorkers that are supervised.

1) Fetcher distributes jobs to URLWorkers in RoundRobin style.

2) Once URLWorker is done with job, it sends it back up to Fetcher.

3) Fetcher than acks the message to the FetchURLQueue.

4) Sends data to FetchedURLQueue to be processed by system.


To scale, just add more fetchers to consume from queue.
