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
package org.camunda.bpm.engine.impl.batch;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Aggregated information on deployment ids and the number of related resources
 */
public class DeploymentMapping {
  protected static String NULL_ID = "$NULL";

  protected String deploymentId;
  protected int count;

  public DeploymentMapping(String deploymentId, int count) {
    this.deploymentId = deploymentId == null ? NULL_ID : deploymentId;
    this.count = count;
  }

  public String getDeploymentId() {
    return NULL_ID.equals(deploymentId) ? null : deploymentId;
  }

  public int getCount() {
    return count;
  }

  public List<String> getIds(List<String> ids) {
    return ids.subList(0, count);
  }

  public void removeIds(int numberOfIds) {
    count -= numberOfIds;
  }

  @Override
  public String toString() {
    return new StringJoiner(";").add(deploymentId).add(String.valueOf(count)).toString();
  }

  /** @return the mapping information as List of Strings */
  public static List<String> toStringList(List<DeploymentMapping> infoList) {
    return infoList == null ? null : infoList.stream().map(DeploymentMapping::toString).collect(Collectors.toList());
  }

  /**
   * @return the List of String-based mapping information transformed into a
   *         list of mapping information objects
   */
  public static List<DeploymentMapping> fromStringList(List<String> infoList) {
    return infoList.stream().map(DeploymentMapping::fromString).collect(Collectors.toList());
  }

  protected static DeploymentMapping fromString(String info) {
    String[] parts = info.split(";");
    if (parts.length != 2) {
      throw new IllegalArgumentException("DeploymentMappingInfo must consist of two parts separated by semi-colons, but was: " + info);
    }
    return new DeploymentMapping(parts[0], Integer.valueOf(parts[1]));
  }
}