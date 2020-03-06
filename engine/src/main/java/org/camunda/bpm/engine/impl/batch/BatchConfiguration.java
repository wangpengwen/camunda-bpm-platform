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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.impl.util.ImmutablePair;


public class BatchConfiguration {

  protected List<String> ids;
  protected List<DeploymentMapping> idMappings;
  protected boolean failIfNotExists;

  public BatchConfiguration(List<String> ids) {
    this(ids, true);
  }

  public BatchConfiguration(List<String> ids, boolean failIfNotExists) {
    this(ids, null, failIfNotExists);
  }

  public BatchConfiguration(List<String> ids, List<DeploymentMapping> mappings) {
    this(ids, mappings, true);
  }

  public BatchConfiguration(List<String> ids, List<DeploymentMapping> mappings, boolean failIfNotExists) {
    this.ids = ids;
    this.idMappings = mappings;
    this.failIfNotExists = failIfNotExists;
  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public List<DeploymentMapping> getIdMappings() {
    return idMappings;
  }

  public void setIdMappings(List<DeploymentMapping> idMappings) {
    this.idMappings = idMappings;
  }

  public boolean isFailIfNotExists() {
    return failIfNotExists;
  }

  public void setFailIfNotExists(boolean failIfNotExists) {
    this.failIfNotExists = failIfNotExists;
  }

  public static class BatchElementConfiguration {
    private SortedMap<String, Set<String>> collectedMappings = new TreeMap<>(Comparator.nullsLast(String::compareToIgnoreCase));

    private List<String> ids;
    private List<DeploymentMapping> mappings;

    public void addDeploymentMappings(List<ImmutablePair<String, String>> mappings) {
      addDeploymentMappings(mappings, null);
    }

    public void addDeploymentMappings(List<ImmutablePair<String, String>> mappings, Collection<String> idList) {
      Set<String> missingIds = idList == null ? null : new HashSet<>(idList);
      mappings.forEach(pair -> {
        String deploymentId = pair.getLeft();
        Set<String> idSet = collectedMappings.get(deploymentId);
        if (idSet == null) {
          idSet = new HashSet<>();
          collectedMappings.put(deploymentId, idSet);
        }
        idSet.add(pair.getRight());
        if (missingIds != null) {
          missingIds.remove(pair.getRight());
        }
      });
      // add missing ids to "null" deployment id
      if (missingIds != null && !missingIds.isEmpty()) {
        Set<String> nullIds = collectedMappings.get(null);
        if (nullIds == null) {
          nullIds = new HashSet<>();
          collectedMappings.put(null, nullIds);
        }
        nullIds.addAll(missingIds);
      }
    }

    public List<String> getIds() {
      if (ids == null) {
        createDeploymentMappings();
      }
      return ids;
    }

    public List<DeploymentMapping> getMappings() {
      if (mappings == null) {
        createDeploymentMappings();
      }
      return mappings;
    }

    public boolean isEmpty() {
      return collectedMappings.isEmpty();
    }

    private void createDeploymentMappings() {
      ids = new ArrayList<>();
      mappings = new ArrayList<>();

      for (Entry<String, Set<String>> mapping : collectedMappings.entrySet()) {
        ids.addAll(mapping.getValue());
        mappings.add(new DeploymentMapping(mapping.getKey(), mapping.getValue().size()));
      }
    }
  }

  public static class DeploymentMapping {
    private static String NULL_ID = "$NULL";

    private String deploymentId;
    private int count;

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

    public List<String> getIds(List<String> ids){
      return ids.subList(0, count);
    }

    public void removeIds(int numberOfIds) {
      count -= numberOfIds;
    }

    @Override
    public String toString() {
      return new StringJoiner(";")
          .add(deploymentId)
          .add(String.valueOf(count))
          .toString();
    }

    public static List<String> toStringList(List<DeploymentMapping> infoList) {
      return infoList == null ? null : infoList.stream().map(DeploymentMapping::toString).collect(Collectors.toList());
    }

    public static List<DeploymentMapping> fromStringList(List<String> infoList) {
      return infoList.stream().map(DeploymentMapping::fromString).collect(Collectors.toList());
    }

    public static DeploymentMapping fromString(String info) {
      String[] parts = info.split(";");
      if (parts.length != 2) {
        throw new IllegalArgumentException("DeploymentMappingInfo must consist of two parts separated by semi-colons, but was: " + info);
      }
      return new DeploymentMapping(parts[0], Integer.valueOf(parts[1]));
    }
  }

}
