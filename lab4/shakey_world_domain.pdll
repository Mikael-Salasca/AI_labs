;; This is a plain STRIPS formulation of the Shakey's world domain.

(define (domain shakey_world_domain)
  (:requirements
   :strips
   )

  (:predicates
  (is-room ?r) ; is the room existing
  (is-shakey ?s) ; is shakey existing
  (is-box ?box) ; is the box existing
  (are-linked ?r1 ?r2 ?d) ; are the rooms linked by a door
  (is-at ?any-objects ?room) ; is any object in the room
  (is-wide-door ?d )        ; is it a wide door
  (is-light-on ?r)     		 ; is light on in the room
   )

; action : shakey moves from one room to another
   (:action moves
     :parameters (?s ?room-a ?room-b ?door)
     :precondition (and
       (is-shakey ?s)(is-at ?s ?room-a)
       (is-room ?room-a)(is-room ?room-b)
       (are-linked ?room-a ?room-b ?door)
     )
     :effect (and
       (is-at ?s ?room-b)
       (not (is-at ?s ?room-a) )
     )
   )

; action : shakey turns the light on
   (:action turns_lights_on
     :parameters (?s ?box ?room)
     :precondition (and
       (is-shakey ?s)(is-at ?s ?room)
       (is-box ?box)(is-at ?box ?room)
       (is-room ?room)
       (not (is-light-on ?room) )
     )
     :effect (is-light-on ?room)
   )

; action : shakey pushes the box
   (:action pushes_box
     :parameters (?s ?box ?room-a ?room-b ?wide-door)
     :precondition (and
       (is-shakey ?s)(is-at ?s ?room-a)
       (is-box ?box)(is-at ?box ?room-a)
       (is-room ?room-a)(is-room ?room-b)
       (is-wide-door ?wide-door)
       (are-linked ?room-a ?room-b ?wide-door)
     )
     :effect (and
       (is-at ?box ?room-b)
       (not (is-at ?box ?room-a) )
     )
   )
)
