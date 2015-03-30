# multiAgents.py
# --------------
# Licensing Information: Please do not distribute or publish solutions to this
# project. You are free to use and extend these projects for educational
# purposes. The Pacman AI projects were developed at UC Berkeley, primarily by
# John DeNero (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and Pieter 
# Abbeel in Spring 2013.
# For more info, see http://inst.eecs.berkeley.edu/~cs188/pacman/pacman.html

from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
        
        #capsuleplaces +
        #distance from pacman to nearest food -
        #distance from pacman to ghost +
        #stop -
        #food number decrease (eat one food) +
        
        
        
        "*** YOUR CODE HERE ***"
        foodList = newFood.asList()
        distance = []
        evaluation = successorGameState.getScore()
        if successorGameState.isWin():#win
            return float("inf")
        if action == 'Stop':#pacman stop
            evaluation -=10
        if successorGameState.getNumFood() < currentGameState.getNumFood():#pacman eat one food
            evaluation += 10
        capsuleplaces = currentGameState.getCapsules()
        if newPos in capsuleplaces:#pacman eat one capsule
            evaluation += 100
        
        for current_food in foodList:
            current_dist = util.manhattanDistance(newPos, current_food)
            distance.append(current_dist)
        min_distance_food = min(distance)
        evaluation -= min_distance_food
        distance_to_ghost = util.manhattanDistance(currentGameState.getGhostPosition(1), newPos)
        if newScaredTimes[0] == 0:
            if distance_to_ghost >10:
                distance_to_ghost = 10
            evaluation +=distance_to_ghost
        return evaluation
        #return successorGameState.getScore()

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        "*** YOUR CODE HERE ***"

        return self.minimax_decision(gameState)
        
        util.raiseNotDefined()
        
        
    def minimax_decision(self,currentGameState):
        
        totalnumberofAgent = currentGameState.getNumAgents()
        ghostNum = totalnumberofAgent - 1
        
        Pacman_legalAction = currentGameState.getLegalActions()#pacman's legal Action
        maxEvaluation = - float("inf")
        next_action = Directions.STOP
        for a in Pacman_legalAction:
            if a == "Stop":
                continue
            successorGameState = currentGameState.generateSuccessor(0,a)#pacman's successorGameState, so that value of index = 0 (Result(state,a))
            current_value = self.min_value(successorGameState,0,1,ghostNum)#current depth = 0, index = 1 for the first ghost, ghostNum = 2
            if current_value > maxEvaluation:
                maxEvaluation = current_value
                next_action = a
        return next_action#pacman's decision
        
    def max_value(self,gameState,depth,ghostNum):
        if self.depth == depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        v = - float("inf")
        
        legalMoves = gameState.getLegalActions()#default index = 0
        for a in legalMoves:
            if a == "Stop":
                continue
            v = max(v, self.min_value(gameState.generateSuccessor(0,a),depth,1,ghostNum) )
        return v
        
    def min_value(self, gameState, depth, current_index, ghostNum):
        if self.depth == depth or gameState.isWin() or gameState.isLose() :
            return self.evaluationFunction(gameState)
        v = float("inf")
        
        legalMoves = gameState.getLegalActions(current_index)#first ghost's legal action
        if current_index < ghostNum:#if the next step is still in the ghost round
            for a in legalMoves:
                if a == "Stop":
                    continue
                v = min(v, self.min_value(gameState.generateSuccessor(current_index,a) , depth, current_index + 1, ghostNum) )
        else:
            for a in legalMoves:
                if a == "Stop":
                    continue
                v = min(v, self.max_value(gameState.generateSuccessor(current_index,a), depth+1,ghostNum))    
        return v
        
class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        return self.alpha_Beta_Search(gameState)
        util.raiseNotDefined()
        



    def alpha_Beta_Search(self,currentGameState):
        
        totalnumberofAgent = currentGameState.getNumAgents()
        ghostNum = totalnumberofAgent - 1
        
        Pacman_legalAction = currentGameState.getLegalActions()#pacman's legal Action
        alpha = - float("inf")
        beta = float("inf")
        maxEvaluation = alpha
        next_action = Directions.STOP
        for a in Pacman_legalAction:
            if a == "Stop":
                continue
            successorGameState = currentGameState.generateSuccessor(0,a)#pacman's successorGameState, so that value of index = 0 (Result(state,a))
            current_value = self.min_value(successorGameState,0,1,ghostNum,alpha,beta)#current depth = 0, index = 1 for the first ghost, ghostNum = 2
            if current_value > maxEvaluation:
                maxEvaluation = current_value
                next_action = a
            if current_value >beta:#different from lecture
                return next_action
            alpha = max(alpha, current_value)
        return next_action#pacman's decision
        
    def max_value(self,gameState,depth,ghostNum,alpha,beta):
        if self.depth == depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        v = - float("inf")
        legalMoves = gameState.getLegalActions()#default index = 0
        for a in legalMoves:
            if a == "Stop":
                continue
            v = max(v, self.min_value(gameState.generateSuccessor(0,a),depth,1,ghostNum,alpha,beta) )
            if v > beta:#different from lecture
                return v
            alpha = max(alpha,v)
        return v
        
    def min_value(self, gameState, depth, current_index, ghostNum,alpha,beta):
        if self.depth == depth or gameState.isWin() or gameState.isLose() :
            return self.evaluationFunction(gameState)
        v = float("inf")
        
        legalMoves = gameState.getLegalActions(current_index)#first ghost's legal action
        if current_index < ghostNum:#if the next step is still in the ghost round
            for a in legalMoves:
                if a == "Stop":
                    continue
                v = min(v, self.min_value(gameState.generateSuccessor(current_index,a) , depth, current_index + 1, ghostNum,alpha,beta) )
                if v < alpha:#different from lecture
                    return v
                beta = min(beta,v)
        else:
            for a in legalMoves:
                if a == "Stop":
                    continue
                v = min(v, self.max_value(gameState.generateSuccessor(current_index,a), depth+1,ghostNum,alpha,beta))
                if v < alpha:#different from lecture
                    return v
                beta = min(beta,v)
        return v







class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        return self.expectimax_decision(gameState)
        util.raiseNotDefined()
    
    def expectimax_decision(self,currentGameState):
        
        totalnumberofAgent = currentGameState.getNumAgents()
        ghostNum = totalnumberofAgent - 1
        
        Pacman_legalAction = currentGameState.getLegalActions()#pacman's legal Action
        maxEvaluation = - float("inf")
        next_action = Directions.STOP
        for a in Pacman_legalAction:
            if a == "Stop":
                continue
            successorGameState = currentGameState.generateSuccessor(0,a)#pacman's successorGameState, so that value of index = 0 (Result(state,a))
            current_value = self.avg_value(successorGameState,0,1,ghostNum)#current depth = 0, index = 1 for the first ghost, ghostNum = 2
            if current_value > maxEvaluation:
                maxEvaluation = current_value
                next_action = a
        return next_action#pacman's decision
        
    def max_value(self,gameState,depth,ghostNum):
        if self.depth == depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        v = - float("inf")
        
        legalMoves = gameState.getLegalActions()#default index = 0
        for a in legalMoves:
            if a == "Stop":
                continue
            v = max(v, self.avg_value(gameState.generateSuccessor(0,a),depth,1,ghostNum) )
        return v
        
    def avg_value(self, gameState, depth, current_index, ghostNum):
        if self.depth == depth or gameState.isWin() or gameState.isLose() :
            return self.evaluationFunction(gameState)
        v = float("inf")
        legalMoves = gameState.getLegalActions(current_index)#first ghost's legal action
        sum = 0
        count = 0
        if current_index < ghostNum:#if the next step is still in the ghost round
            for a in legalMoves:
                if a == "Stop":
                    continue
                count += 1
                sum += self.avg_value(gameState.generateSuccessor(current_index,a) , depth, current_index + 1, ghostNum) 
            v = float(sum)/count
        else:
            for a in legalMoves:
                if a == "Stop":
                    continue
                count += 1
                sum += self.max_value(gameState.generateSuccessor(current_index,a), depth+1,ghostNum)
            v = float(sum)/count
        return v
    
    
    
    
    
    
    
def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    if currentGameState.isWin():
        return float("inf")
    if currentGameState.isLose():
        return -float("inf")
    
    
    evaluation = currentGameState.getScore()
    Food = currentGameState.getFood()
    foodList = Food.asList()
    foodNum = len(foodList)
    
    distance_food = []
    currentPos = currentGameState.getPacmanPosition()
    for current_food in foodList:
        current_dist = util.manhattanDistance(currentPos, current_food)
        distance_food.append(current_dist)
    min_distance_food = min(distance_food)
    
    ScaredTimes = []
    
    ghostStates = currentGameState.getGhostStates()
    for ghostState in ghostStates:#scared time of each ghost
        ScaredTimes.append(ghostState.scaredTimer)
    
    
    ghostPositions = currentGameState.getGhostPositions()#ghost position
    pacman_ghost_distance = []#distance from pacman's current to each ghost
    for ghostPos in ghostPositions:
        pacman_ghost_distance.append(util.manhattanDistance(currentPos, ghostPos))
    min_distance_ghost = min(pacman_ghost_distance)
    
    capsuleplaces = currentGameState.getCapsules()
    pacman_capsule_distance = []#distance from pacman's current to each capsule
    for capsulePos in capsuleplaces:
        pacman_capsule_distance.append(util.manhattanDistance(currentPos, capsulePos))
    min_distance_capsule = 1
    if len(pacman_capsule_distance) != 0:
        min_distance_capsule = min(pacman_capsule_distance)
    
    evaluation = evaluation - min_distance_food
    evaluation = evaluation - min_distance_capsule * 10
    evaluation = evaluation - foodNum
    if ScaredTimes[0]<=0 and min_distance_ghost<10:
        evaluation = evaluation + min_distance_ghost * 4
    
    return evaluation
    
    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction

class ContestAgent(MultiAgentSearchAgent):
    """
      Your agent for the mini-contest
    """

    def getAction(self, gameState):
        """
          Returns an action.  You can use any method you want and search to any depth you want.
          Just remember that the mini-contest is timed, so you have to trade off speed and computation.

          Ghosts don't behave randomly anymore, but they aren't perfect either -- they'll usually
          just make a beeline straight towards Pacman (or away from him if they're scared!)
        """
        "*** YOUR CODE HERE ***"
        util.raiseNotDefined()

