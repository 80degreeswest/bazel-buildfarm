// Copyright 2020 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package build.buildfarm.metrics.tracing.honeycomb;

import build.buildfarm.metrics.tracing.BuildFarmTracing;
import io.honeycomb.beeline.tracing.Beeline;
import io.honeycomb.beeline.tracing.SpanBuilderFactory;
import io.honeycomb.beeline.tracing.SpanPostProcessor;
import io.honeycomb.beeline.tracing.Tracer;
import io.honeycomb.beeline.tracing.Tracing;
import io.honeycomb.beeline.tracing.sampling.Sampling;
import io.honeycomb.libhoney.HoneyClient;
import io.honeycomb.libhoney.LibHoney;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoneycombPublisher implements BuildFarmTracing {
  private static final Logger logger = Logger.getLogger(HoneycombPublisher.class.getName());

  private HoneyClient client = null;
  private Beeline beeline = null;

  public HoneycombPublisher(String writeKey, String dataset) {
    if (!writeKey.isEmpty() && !dataset.isEmpty()) {
      client =
          LibHoney.create(LibHoney.options().setDataset(dataset).setWriteKey(writeKey).build());
      SpanPostProcessor postProcessor =
          Tracing.createSpanProcessor(client, Sampling.alwaysSampler());
      SpanBuilderFactory factory =
          Tracing.createSpanBuilderFactory(postProcessor, Sampling.alwaysSampler());
      Tracer tracer = Tracing.createTracer(factory);
      beeline = Tracing.createBeeline(tracer, factory);
      logger.log(Level.INFO, String.format("Initialized Honeycomb tracer with dataset %s: %s", dataset, beeline.toString()));
    }
  }

  @Override
  public void sendTrace(String key, String value) {
    logger.info(String.format("Sending trace: %s=%s", key, value));
    if (beeline != null) {
      beeline.getActiveSpan().addField(key, value);
      logger.info(String.format("Sent trace: %s=%s", key, value));
    }
  }
}