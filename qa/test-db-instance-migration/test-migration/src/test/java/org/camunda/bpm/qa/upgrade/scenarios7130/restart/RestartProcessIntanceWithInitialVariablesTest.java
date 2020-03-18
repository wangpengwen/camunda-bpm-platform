/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.qa.upgrade.scenarios7130.restart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RestartProcessIntanceWithInitialVariablesTest {

  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule("camunda.cfg.xml");

  RuntimeService runtimeService;

  @Before
  public void assignServices() {
    runtimeService = engineRule.getRuntimeService();
  }

  @Test
  public void shouldRestartWithInitialVariables() throws ParseException {
    ProcessInstance processInstanceWithInitialVariables = runtimeService.createProcessInstanceQuery()
        .processInstanceBusinessKey("ProcessIntanceWithInitialVariables")
        .singleResult();
    assertNotNull(processInstanceWithInitialVariables);

    runtimeService.deleteProcessInstance(processInstanceWithInitialVariables.getId(), "test");
    // when
    runtimeService.restartProcessInstances(processInstanceWithInitialVariables.getProcessDefinitionId())
      .startBeforeActivity("theTask")
      .processInstanceIds(processInstanceWithInitialVariables.getId())
      .initialSetOfVariables()
      .execute();

    ProcessInstance restartedProcessInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceBusinessKey("ProcessIntanceWithInitialVariables")
        .active()
        .singleResult();

    // then
    restartedProcessInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceBusinessKey("ProcessIntanceWithInitialVariables")
        .active()
        .singleResult();
    List<VariableInstance> variables = runtimeService.createVariableInstanceQuery().processInstanceIdIn(restartedProcessInstance.getId()).list();
    assertEquals(1, variables.size());
    assertEquals("var1", variables.get(0).getName());
    assertEquals("value1", variables.get(0).getValue());
//    restartedProcessInstance = runtimeService.createProcessInstanceQuery()
//        .processInstanceBusinessKey("ProcessIntance")
//        .active()
//        .singleResult();
//    variables = runtimeService.createVariableInstanceQuery().processInstanceIdIn(restartedProcessInstance.getId()).list();
//    assertEquals(0, variables.size());
  }
}
