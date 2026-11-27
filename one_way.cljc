(ns e5.one-way
  (:require [statecharts.core :as fsm]))

;; The running/stopping modes are controller states. The nested leaf
;; states :r, :ra, :g, :a encode the visible signal colour.
;; A halt request never changes the lamp colour directly; only :next does.
(def one-way
  (fsm/machine
   {:id :one-way
    :initial :stopped
    :states
    {:stopped {:on {:run [:> :running :r]
                    :next :stopped}}
     :running
     {:initial :r
      :on {:run {}}
      :states
      {:r  {:on {:next :ra :halt [:> :stopped]}}
       :ra {:on {:next :g :halt [:> :stopping :ra]}}
       :g  {:on {:next :a :halt [:> :stopping :g]}}
       :a  {:on {:next :r :halt [:> :stopping :a]}}}}
     :stopping
     {:initial :ra
      :states
      {:ra {:on {:next :g}}
       :g  {:on {:next :a}}
       :a  {:on {:next [:> :stopped]}}}}}}))

(comment
  (def service (fsm/service one-way))
  (fsm/start service)
  (fsm/send service :run)
  (fsm/send service :next)
  (fsm/send service :next)
  (fsm/send service :halt) ; request graceful stop from green
  (fsm/send service :next) ; amber
  (fsm/send service :next) ; stopped at red
  (fsm/value service))
