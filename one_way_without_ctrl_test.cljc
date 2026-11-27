(ns e5.one-way-without-ctrl-test
  (:require [clojure.test :refer [deftest is testing]]
            [statecharts.core :as fsm]
            [e5.one-way-without-ctrl :refer [one-way-without-ctrl]]))

(defn trace [events]
  (reductions (fn [state event] (fsm/transition one-way-without-ctrl state event))
              (fsm/initialize one-way-without-ctrl)
              events))

(deftest one-signal-requirements
  (testing "R4 R6 R7: initial red, next-only UK colour order"
    (is (= [:r :ra :g :a :r :ra :g]
           (mapv :_state (trace (repeat 6 :next))))))
  (testing "R8: green recurs within four next events, without starvation"
    (is (= 2 (count (take-while #(not= :g %)
                               (map :_state (trace (repeat 12 :next))))))))
  (testing "R3: no redundant next transition in four-step colour cycle"
    (is (= :r (:_state (last (trace (repeat 4 :next))))))
    (is (= :g (:_state (last (trace (repeat 2 :next)))))))
  (testing "R6: unsupported control events are rejected"
    (is (thrown? clojure.lang.ExceptionInfo
                 (doall (trace [:run]))))
    (is (thrown? clojure.lang.ExceptionInfo
                 (doall (trace [:halt]))))))
