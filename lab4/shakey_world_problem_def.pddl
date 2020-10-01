;; This is a concrete problem formulation of the Shakey's world domain.

;; Usage: ./FF-v2.3/ff -o shakey_world_domain.pddl -f shakey_world_problem_def.pddl

(define (problem shakey_world_problem_def)
  (:domain shakey_world_domain)

  (:objects
	r1 r2 r3	; room
	d1 d2	d3	; door
	b   		; box
	s		 ; shackey
  )

  (:init
  (is-room r1) (is-room r2) (is-room r3)
  (is-wide-door d1) (is-wide-door d2) (not(is-wide-door d3))
  (are-linked r1 r2 d1) (are-linked r2 r3 d2) (are-linked r2 r3 d3)
  (are-linked r2 r1 d1) (are-linked r3 r2 d2) (are-linked r3 r2 d3)
	(is-shakey s) (is-at s r2)
	(is-box b)(is-at b r1)
  (not (is-light-on r1) )
  (not (is-light-on r2) )
  (not (is-light-on r3) )
  )

  (:goal
  (and (is-light-on r1)
       (is-light-on r2)
       (is-light-on r3))
  )
)