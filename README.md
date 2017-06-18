# Finding nearest airport for the user

Test assignment for travel audience company

# Compiling

The project could be compiled using sbt

    sbt compile test

# Running

To run main script you must provide next parameters:

* --airport-file csv with airport data (script expect header as first line)
* --user-file csv with users data (script expect header as first line)
* --result-file resulting file (could be omitted, default value *result.csv" will be used)

## Running example:

    sbt "runMain loki256.github.com.userairport.Main --airport-file optd-sample-20161201.csv --user-file sample_data.csv --result-file brute_result.csv"

# Description

I used geohash library for implementation. We assume that airport data set is significantly smaller then input users (it's only 6889 items). For each airport we create 9 [geohashes](https://en.wikipedia.org/wiki/Geohash) for each geohash accuracy. One for the location itself and 8 surrounding geohashes. We put them into dict: geohashString -> Sequence(Airport). For each user we calculate geohash and search it in the map. If we did not find close airports we increase radius (decrease geohash size) and try again. If we managed to find airports by geohash we choose the closest one by brute force. 

Complexity of algorithm is about O(NxLog(M))

# Algorithm scaling
To scale the algorithm we have to copy airports to each worker (for example in case of using spark we would put airports to broadcast variable) and divide input users to parts. Each part could be calculated completely independently.

