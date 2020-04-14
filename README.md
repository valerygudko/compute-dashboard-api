# Compute Dashboard Api
An application which exposes a RESTful API for a custom dashboard showing all of active EC2 instances for a specified region.
It accepts page and sort requests with valid parameters, but both requests are optional, when region is mandatory.

The aim of this application is to give auditors visibility into our cloud
infrastructure without having to give them access to the AWS Console.

## Get up and running

1. Clone the project locally

2. Ensure that you have:
   * java installed on your system (1.8)
   * gradle (this was built with Gradle 6.3)
   * all secrets populated in application.yml:
        * okta application credentials
        * password for ssl keystore.p12 
        * aws keys for authentication (in production for the application deployed to ec2 instance I would use IAM Role instead)
        
3. Security
    * This application has SSL enabled for all calls with self-signed certificate located in `/resources/keystore.p12`
    * It also uses oauth2 with Okta.

4. Run application using the following:

``` ./gradlew clean bootRun ```

   With the backend running you can get the list of running ec-2 instances for region `eu-west-2` by calling: `http://localhost:8080/compute-dashboard-api/v1/ec2-instances?region=eu-west-2`
   
## API:

   `GET /compute-dashboard-api/v1/ec2-instances?region=eu-west-2` - simplest request, uses default `page` size = 5 and referencing 1st page. Result by default are not sorted.
 
* Request parameter. Paginating and sorting:
    
 1. Sample call with a page request (as per SSL config all request redirect to https on port 8443): 
            
        https://localhost:8443/compute-dashboard-api/v1/ec2-instances?region=eu-west-2&page=1&size=5
    
     * min page size = 5 and max is 1000, min page number is 1.
     
 2. Sample call with a sort request: 
                 
        https://localhost:8443/compute-dashboard-api/v1/ec2-instances/?region=eu-west-2&sort.attr=launchTime&sort.order=desc
         
     * both sort params (`sort.attr` and `sort.order`) should be provided with a sort request
     * multiple sorting attributes with sort orders are supported: `sort.attr=id&sort.order=asc&sort.attr=type&sort.order=asc`
     * output list is sorted accordingly (both in api response and console table output)
     * result list is sortable by all attributes. Full list of attributes is:
     ` name, id, type, state, monitoring, az, publicIP, privateIP, subnetId, launchTime ` with sort order: `asc, desc`
     
 Response status code
 `200 - OK`
 
 * Produces
     `application/json` as it's RESTful api. 
 * Response object
     `EC2InstanceResponse[].class`
 * Example value of response:
 ```
 [
     {
         "name": "NameHere",
         "id": "i-0a836ccf37fedeb90",
         "type": "t2.micro",
         "state": "running",
         "monitoring": "disabled",
         "az": "eu-west-2a",
         "publicIP": "3.8.171.135",
         "privateIP": "172.31.20.163",
         "subnetId": "subnet-1dfd7767",
         "launchTime": "2020-04-13T11:49:27"
     }
 ]
 ```
 * And printed table to console:
 
    NAME   | ID   |  TYPE   | STATE  | MONITORING| AZ | PUBLIC IP| PRIVATE IP | SUBNET ID | LAUNCH TIME
     ------ | ----- | ---- | ---- |----- | ------ | ----- | ---- | ------ | ----
     NameHere| i-0a836ccf37fedeb90| t2.micro| running| disabled  | eu-west-2a| 3.8.171.135| 172.31.20.163| subnet-1dfd7767| 2020-04-13T11:49:27
    
 
 * Api Errors:
 
 `302` - ```Redirect to Okta for login```
 
 `400` - ```Request couldn't be parsed```
 
 `422` - ```Response couldn't be processed```
 
 `500` - ```Communication Failed``` or other ```Internal server error```
 

