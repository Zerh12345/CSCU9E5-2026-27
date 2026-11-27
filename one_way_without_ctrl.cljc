(ns e5.one-way-without-ctrl
  (:require [statecharts.core :as fsm]))

;; One signal. No run/halt controller (requirements R1-R8 only).
(def one-way-without-ctrl
  (fsm/machine
   {:id :one-way-without-ctrl
    :initial :r
    :states {:r  {:on {:next :ra}}
             :ra {:on {:next :g}}
             :g  {:on {:next :a}}
             :a  {:on {:next :r}}}}))

(comment
  (def service (fsm/service one-way-without-ctrl))
  (fsm/start service)
  (fsm/value service) ; :r
  (fsm/send service :next)
  (fsm/value service) ; :ra
  )
