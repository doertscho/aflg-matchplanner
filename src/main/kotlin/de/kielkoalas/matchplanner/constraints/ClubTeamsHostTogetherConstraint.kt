package de.kielkoalas.matchplanner.constraints

import com.google.ortools.linearsolver.MPSolver
import de.kielkoalas.matchplanner.ConstraintSet
import de.kielkoalas.matchplanner.buildImplicationConstraint
import de.kielkoalas.matchplanner.models.Problem
import de.kielkoalas.matchplanner.models.getAllGroups
import de.kielkoalas.matchplanner.variables.Host

class ClubTeamsHostTogetherConstraint(private val problem: Problem) : ConstraintSet {

    override fun createInSolver(solver: MPSolver) {
        for ((matchDay, groupNo) in problem.getAllGroups("w")) {

//            val mSpec = matchDay.specByCompetition["m"]
//            if (mSpec?.groupSize == 2) continue

            for (team in problem.teams.filter { it.competition == "w" && it.clubs.size == 1 }) {
                val key = "hostTogether-${matchDay.number}-$groupNo-${team.abbreviation}"
                val wHostVariable = Host.get(solver, matchDay, groupNo, team)
                val mensTeam = problem.teams.find { it.clubs == team.clubs && it.competition == "m" }
                    ?: error("could not find men's team for $team")
                val mHostVariable = Host.get(solver, matchDay, groupNo, mensTeam)
                solver.buildImplicationConstraint(key, wHostVariable, mHostVariable)
            }

            // joint teams should play with one of their host clubs
            for (team in problem.teams.filter { it.competition == "w" && it.clubs.size > 1 }) {
                val key = "hostTogether-${matchDay.number}-$groupNo-${team.abbreviation}"
                val wHostVariable = Host.get(solver, matchDay, groupNo, team)
                val mensTeams = problem.teams.filter {
                    it.clubs.intersect(team.clubs).isNotEmpty() && it.competition == "m"
                }
                val constraint = solver.makeConstraint(-1.0, 0.0, key)
                constraint.setCoefficient(wHostVariable, 1.0)
                mensTeams.forEach { mensTeam ->
                    val mHostVariable = Host.get(solver, matchDay, groupNo, mensTeam)
                    constraint.setCoefficient(mHostVariable, -1.0)
                }
            }
        }
    }
}
