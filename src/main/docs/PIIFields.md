IMPORTANT NOTE
======================
**I am NOT a lawyer.
I just try to understand as best as I can.
What I write here is how I understand it from my perspective here in The Netherlands.
Your lawyer will most likely disagree. 
So use this feature with caution.**

Is the Useragent Personally identifiable (PII) ?
======================
How I understand the term PII ([Personally identifiable information](https://en.wikipedia.org/wiki/Personally_identifiable_information))
is that (a combination of) fields that allow identifying an individual is considered to be a problem.

Some people have told me that they consider the UserAgent string to be a PII field because some of them are extremely unique.

Looking at the real values you get from the browser there is reason to believe that some are actually so unique that they effectively have become some kind of unique browser id.

For people using the latest Chrome on the most common operating system (Windows 7/10) I believe this is not a problem because everyone has the exact same one.

> Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3346.8 Safari/537.36

However for people with a lot of plugins the level of uniqueness increases.  

> Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E; BRI/2; eSobiSubscriber 1.0.0.40; MAAR; Media Center PC 6.0; SLCC2; ms-office)

Sometimes the useragent even includes a UUID making it guaranteed unique.  

> Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; SIMBAR={51037e31-26af-4b94-9d65-77ef810e55bb}; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)

and between these there are lots of greyscales.

So in general I think that for some visitors the Useragent is actually a PII field.


To allow people to have an easier way of knowing which fields can be used to effectively un-PII the useragent I have created
a list of the field names that I believe to be 'safe enough'. 

**Important here is this is what _I_ believe, like I said: I am NOT a lawyer so I may very well be wrong about this.**

What I have done is that there is now a special field you can ask for 

    DropPII

If you ask for this field you will ONLY be allowed to ask for the fields that I think are safe.
Note you will get an additional field `DropPII` in the output because that way all UDFs can use this feature unchanged.

In the Java API there is also the builder method .dropPIIFields()
 - If you do not specify any fields and only do the dropPIIFields you will get all fields that have been white listed. 
 - If you do specify fields in combination with dropPIIFields the system will fail if you ask for a field not on the whitelist.
