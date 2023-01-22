# Lib responsiblities

- Logging
- Runs hardware loops that are independent of the robot logic
    - for example, motor encoder + abs encoder realignment every 10s

Eventually BucketLib will go away and be absorbed into BootstrapRobot

## Long Term Goals

- ai brain

# How to

- make your class
- make a class called (your class here)Setup
- make that class implement ISetup<(your class here)>
- implement the setup method, and make sure that in the setup method you instantiate your class and return it
- register loggers
- idk do other stuff im too tired to write good docs
