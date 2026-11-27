(ns e5.three-way
  (:require [statecharts.core :as fsm]))

;; Three coordinated approaches in fair order: :w -> :n -> :s -> :w.
;; At most one approach has a non-red colour. When a signal is active,
;; other signals remain :r. The leaf states are literal lamp colours.
;; All-red is represented by :stopped or by an active turn's :r leaf.
(def ^:private order [:w :n :s])

(defn- after [signal]
  (case signal :w :n :n :s :s :w))

(defn- active-turn [signal]
  (let [following (after signal)
        stop-key (keyword (str "stopping-" (name signal)))]
    {:initial :r
     :states
     {:r  {:on {:next :ra :halt [:> :stopped]}}
      :ra {:on {:next :g :halt [:> stop-key :ra]}}
      :g  {:on {:next :a :halt [:> stop-key :g]}}
      :a  {:on {:next [:> following :r]
                :halt [:> stop-key :a]}}}}))

(defn- stopping-turn []
  {:initial :ra
   :states {:ra {:on {:next :g}}
            :g  {:on {:next :a}}
            :a  {:on {:next [:> :stopped]}}}})

(def three-way
  (fsm/machine
   {:id :three-way
    :initial :stopped
    :states (merge
             {:stopped {:on {:run [:> :w :r]}}}
             (into {} (map (fn [signal] [signal (active-turn signal)]) order))
             (into {} (map (fn [signal]
                             [(keyword (str "stopping-" (name signal)))
                              (stopping-turn)]) order)))}))

(defn displayed-colours
  "Convert a machine state value into the visible colours {:w :r :n :r :s :r}."
  [value]
  (let [all-red {:w :r :n :r :s :r}]
    (if (vector? value)
      (let [[phase colour] value
            signal (if (contains? #{:w :n :s} phase)
                     phase
                     (case phase
                       :stopping-w :w
                       :stopping-n :n
                       :stopping-s :s
                       nil))]
        (if signal (assoc all-red signal colour) all-red))
      all-red)))

(comment
  (def service (fsm/service three-way))
  (fsm/start service)
  (fsm/send service :run)
  (dotimes [_ 6] (fsm/send service :next))
  (displayed-colours (fsm/value service)))
