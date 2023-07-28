/*
 * Copyright 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package com.example.noframeworks.bigtable;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.Table;
import com.google.cloud.bigtable.admin.v2.stub.BigtableTableAdminStubSettings;
import com.google.cloud.bigtable.admin.v2.stub.EnhancedBigtableTableAdminStub;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.BigtableEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class BigtableIntegrationTests {

  public static final String PROJECT_ID = "test-project";

  public static final String INSTANCE_ID = "test-instance";

  @Rule
  // emulatorContainer {
  public BigtableEmulatorContainer emulator = new BigtableEmulatorContainer(
      DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:emulators")
  );

  // }

  @Test
  // testWithEmulatorContainer {
  public void testSimple() throws IOException, InterruptedException, ExecutionException {
    ManagedChannel channel = ManagedChannelBuilder.forTarget(emulator.getEmulatorEndpoint()).usePlaintext().build();

    TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(
        GrpcTransportChannel.create(channel)
    );
    NoCredentialsProvider credentialsProvider = NoCredentialsProvider.create();

    try {
      createTable(channelProvider, credentialsProvider, "test-table");

      BigtableDataClient client = BigtableDataClient.create(
          BigtableDataSettings
              .newBuilderForEmulator(emulator.getHost(), emulator.getEmulatorPort())
              .setProjectId(PROJECT_ID)
              .setInstanceId(INSTANCE_ID)
              .build()
      );

      client.mutateRow(RowMutation.create("test-table", "1").setCell("name", "firstName", "Dan"));

      Row row = client.readRow("test-table", "1");
      List<RowCell> cells = row.getCells("name", "firstName");

      assertThat(cells).isNotNull().hasSize(1);
      assertThat(cells.get(0).getValue().toStringUtf8()).isEqualTo("Dan");
    } finally {
      channel.shutdown();
    }
  }

  // }

  // createTable {
  private void createTable(
      TransportChannelProvider channelProvider,
      CredentialsProvider credentialsProvider,
      String tableName
  ) throws IOException {
    EnhancedBigtableTableAdminStub stub = EnhancedBigtableTableAdminStub.createEnhanced(
        BigtableTableAdminStubSettings
            .newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider)
            .build()
    );

    try (BigtableTableAdminClient client = BigtableTableAdminClient.create(PROJECT_ID, INSTANCE_ID, stub)) {
      Table table = client.createTable(CreateTableRequest.of(tableName).addFamily("name"));
    }
  }
  // }
}
