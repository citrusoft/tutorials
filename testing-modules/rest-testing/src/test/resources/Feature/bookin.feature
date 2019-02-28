Feature: Bookin Scripts
  Developers should be able to submit a script to DBScripts, represented by WireMock
  
  Background: 
    Given a list of commits
    | token  | user  | email                  | repo | project | commit |
    | bookin | thunt | tommy.hunt@verizon.com | NTS  | GHLV    | fd234  |
    | bookin | jkies | james.kies@verizon.com | GTS  | FOTV    |        |

  Scenario: Valid Bookin scripts should return ok status   
    When developer commits script 1
    Then the server should accept it and return OK
    
  Scenario: InValid Bookin scripts should return error status   
    When developer commits script 2
    Then the server should accept it and return ERROR
    
    
